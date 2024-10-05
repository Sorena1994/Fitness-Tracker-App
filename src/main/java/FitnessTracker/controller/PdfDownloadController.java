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

        // Create PDF in memory
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {

            // Set document metadata
            pdfDocument.getDocumentInfo().setTitle("Exercise Data");
            pdfDocument.getDocumentInfo().setAuthor("Fitness Tracker App");
            pdfDocument.getDocumentInfo().setCreator("Fitness Tracker Development Team");

            // Add content to the PDF
            document.add(new Paragraph("Exercise Data\n\n"));
            Paragraph paragraph = new Paragraph("This PDF file contains your exercise history. Hope it helps!\n\n");

            // Query to retrieve data from the database
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

            // Create table with 4 columns (id, exercise_name, weight, date)
            Table table = new Table(4);
            addTableHeader(table, "ID");
            addTableHeader(table, "Exercise Name");
            addTableHeader(table, "Weight (kg)");
            addTableHeader(table, "Date");

            // Add data to the PDF table
            for (Map<String, Object> rowFirstQuery : rowsFirstQuery) {
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("exercise_id").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("exercise_name").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("weight").toString())));
                table.addCell(new Cell().add(new Paragraph(rowFirstQuery.get("date").toString())));
            }

            // Add table to document
            document.add(table);

            // Creating graphs
            List<Map<String, Object>> rowsGraphsQuery = jdbcTemplate.queryForList(""" 
                    SELECT exercise_name,
                           GROUP_CONCAT(weight ORDER BY date) AS weights,
                           GROUP_CONCAT(date ORDER BY date) AS dates
                           FROM exercise
                           GROUP BY exercise_name""");

            for (Map<String, Object> row : rowsGraphsQuery) {
                String exerciseName = (String) row.get("exercise_name");
                String[] weights = ((String) row.get("weights")).split(",");

                // Creating a List of Numbers
                List<Number> weightValues = new ArrayList<>();
                for (String weight : weights) {
                    weightValues.add(Double.parseDouble(weight.trim()));
                }

                // Check if dates are already Date objects or if they need to be parsed
                List<Date> dateValues = new ArrayList<>();
                Object dateData = row.get("dates");

                if (dateData instanceof String) {
                    // Dates are stored as a comma-separated string, so we need to parse
                    String[] dateStrings = ((String) dateData).split(",");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (String dateStr : dateStrings) {
                        try {
                            dateValues.add(dateFormat.parse(dateStr.trim()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (dateData instanceof Date[]) {
                    // Dates are stored as a Date array, so we can cast directly
                    Date[] dates = (Date[]) dateData;
                    dateValues.addAll(Arrays.asList(dates));
                }

                // Create the chart
                XYChart chart = new XYChart(800, 600);
                chart.setTitle(exerciseName);
                chart.setXAxisTitle("Date");
                chart.setYAxisTitle("Weights (kg)");
                chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

                // Add data to the chart
                chart.addSeries("Weights", dateValues, weightValues);



            // Save the chart as an image
                BufferedImage image = BitmapEncoder.getBufferedImage(chart);
                ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imageOutputStream);
                byte[] imageBytes = imageOutputStream.toByteArray();

                // Create a new area break to ensure that title and image are on the same page if necessary
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                // Add the title paragraph followed immediately by the chart image
                Paragraph titleParagraph = new Paragraph(exerciseName)
                        .setFontSize(14)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5);  // Minimal margin to ensure it's close to the chart

                // Add title and chart as a single block
                document.add(titleParagraph);

                // Center and add chart image directly below the title
                Image imageDoc = new Image(ImageDataFactory.create(imageBytes))
                        .setHorizontalAlignment(HorizontalAlignment.CENTER)
                        .setAutoScale(true)  // Ensures the image fits within the page margins
                        .setMarginBottom(10);  // Margin for space after the chart

                document.add(imageDoc);
            }

        } // Resources are automatically closed here

        // Convert to InputStream for download
        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=ExerciseData.pdf");

        // Return PDF as a response
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
