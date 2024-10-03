package FitnessTracker.controller;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.apache.commons.lang3.time.DateUtils; // Import for parsing dates
import java.text.ParseException; // Import for handling parse exceptions
import java.util.Date; // Import for using Date class
import java.util.List; // Import for List
import java.util.ArrayList; // Import for ArrayList
import java.util.Map; // Import for Map

@RestController
@RequestMapping("/api")
public class PdfDownloadController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/download-pdf")
    public ResponseEntity<InputStreamResource> downloadPdf(@RequestParam(required = false) String userId)
            throws IOException, InterruptedException {

        // Create PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {

            pdfDocument.getDocumentInfo().setTitle("Exercise Data");
            pdfDocument.getDocumentInfo().setAuthor("Fitness Tracker App");
            pdfDocument.getDocumentInfo().setCreator("Fitness Tracker Development Team");

            document.add(new Paragraph("Exercise Data\n\n"));
            Paragraph paragraph = new Paragraph("This PDF file contains your exercise history. Hope it helps!\n\n");

            List<Map<String, Object>> rowsFirstQuery;
            if (userId != null) {
                rowsFirstQuery = jdbcTemplate.queryForList(
                        "SELECT exercise_id, exercise_name, weight, date FROM exercise WHERE user_id = ?", userId);
            } else {
                rowsFirstQuery = jdbcTemplate.queryForList("SELECT exercise_id, exercise_name, weight, date FROM exercise");
            }

            List<Map<String, Object>> rowsSecondQuery = jdbcTemplate.queryForList(
                    "SELECT MIN(date) AS first_day, MAX(date) AS last_day FROM exercise");

            if (!rowsSecondQuery.isEmpty()) {
                Map<String, Object> rowSecondQuery = rowsSecondQuery.get(0);
                paragraph.add("The first day of your workout was " + rowSecondQuery.get("first_day").toString() + ". ");
                paragraph.add("The last day of your workout was " + rowSecondQuery.get("last_day").toString() + ".\n");
            }

            document.add(paragraph);

            // Create table with these 4 columns (id, exercise_name, weight, date), exactly as database
            Table table = new Table(4);
            addTableHeader(table, "ID");
            addTableHeader(table, "Exercise Name");
            addTableHeader(table, "Weight (kg)");
            addTableHeader(table, "Date");

            // Fill the PDF table with relevant data
            for (Map<String, Object> rowFirstQuery : rowsFirstQuery) {
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("exercise_id").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("exercise_name").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("weight").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("date").toString())));
            }

            document.add(table);

            // Creating graphs and adding them to the PDF file
            List<Map<String, Object>> rowsGraphsQuery = jdbcTemplate.queryForList(""" 
                    SELECT exercise_name,
                           GROUP_CONCAT(weight ORDER BY date) AS weights,
                           GROUP_CONCAT(date ORDER BY date) AS dates
                           FROM exercise
                           GROUP BY exercise_name""");

            for (Map<String, Object> row : rowsGraphsQuery) {
                String exerciseName = (String) row.get("exercise_name");
                String[] weights = ((String) row.get("weights")).split(",");
                List<Number> weightValues = new ArrayList<>();
                for (String weight : weights) {
                    weightValues.add(Double.parseDouble(weight.trim()));
                }
                List<Date> dateValues = new ArrayList<>();
                Object dateData = row.get("dates");
                Date[] dates = (Date[]) dateData;
                dateValues.addAll(Arrays.asList(dates));
                
                XYChart chart = new XYChart(800, 600);
                chart.setTitle(exerciseName);
                chart.setXAxisTitle("Date");
                chart.setYAxisTitle("Weights (kg)");
                chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
                chart.addSeries("Weights", dateValues, weightValues);

                BufferedImage image = BitmapEncoder.getBufferedImage(chart);
                ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imageOutputStream);
                byte[] imageBytes = imageOutputStream.toByteArray();
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                Paragraph titleParagraph = new Paragraph(exerciseName)
                        .setFontSize(14)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5);  // Minimal margin to ensure it's close to the chart
                document.add(titleParagraph);

                Image imageDoc = new Image(ImageDataFactory.create(imageBytes))
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setAutoScale(true)  
                        .setMarginBottom(10);  

                document.add(imageDoc);
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ExerciseData.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private void addTableHeader(Table table, String headerTitle) {
        Cell header = new Cell();
        header.add(new Paragraph(headerTitle).setBold()); // Make the header bold
        table.addHeaderCell(header);
    }
}
