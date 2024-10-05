import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.SwingWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FixedDateChartExample {

    public static void main(String[] args) throws ParseException {

        // Create an XY chart with Date on x-axis
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Fixed Date X-Axis Example").xAxisTitle("Date").yAxisTitle("Value").build();

        // Fixed Date values (String array)
        String[] dateStrings = {"2024-01-01", "2024-02-01", "2024-03-01", "2024-04-01", "2024-05-01"};

        // Convert String dates to Date objects
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date[] dates = new Date[dateStrings.length];
        for (int i = 0; i < dateStrings.length; i++) {
            dates[i] = dateFormat.parse(dateStrings[i]); // Parse each date string
        }

        // Fixed Y values corresponding to the Date array
        List<Double> yData = Arrays.asList(10.0, 20.0, 30.0, 25.0, 15.0);

        // Add series to chart
        XYSeries series = chart.addSeries("Fixed Data", Arrays.asList(dates), yData);

        // Show chart
        new SwingWrapper<>(chart).displayChart();
    }
}
