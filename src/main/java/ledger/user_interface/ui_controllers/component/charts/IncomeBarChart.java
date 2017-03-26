package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ledger.database.entity.Transaction;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Bar chart that shows positive
 */
public class IncomeBarChart extends javafx.scene.chart.BarChart<Date, Double> implements IChart {

    private static Axis defaultXAxis() {
        Axis xAxis = new CategoryAxis();
        xAxis.setSide(Side.BOTTOM);

        return xAxis;
    }

    private static Axis defaultYAxis() {
        Axis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);

        return yAxis;
    }

    public IncomeBarChart(List<Transaction> data) {
        this(defaultXAxis(),defaultYAxis());
        this.updateData(data);
    }


    public IncomeBarChart(@NamedArg("xAxis") Axis<Date> dateAxis, @NamedArg("yAxis") Axis<Double> longAxis) {
        super(dateAxis, longAxis);
    }

    @Override
    public void updateData(List<Transaction> transactionList) {
        XYChart.Series series = new XYChart.Series<Date,Long>();
        for(Transaction transaction: transactionList) {
            double amount = transaction.getAmount() / 100.0;
            if(amount < 0)
                continue;
            series.getData().add(new XYChart.Data(transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth().toString(), amount));
        }

        getData().add(series);

        series.setName("Income");

        this.getData().setAll(series);
    }
}
