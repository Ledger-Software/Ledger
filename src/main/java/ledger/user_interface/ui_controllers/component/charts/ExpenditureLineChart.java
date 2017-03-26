package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Side;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ledger.database.entity.Transaction;

import java.util.*;

/**
 * Line Chart that shows monthly differentials
 */
public class ExpenditureLineChart extends LineChart<Long ,Double> implements IChart {

    private static Axis defaultXAxis() {
        Axis xAxis = new DateAxis();
        xAxis.setSide(Side.BOTTOM);

        return xAxis;
    }

    private static Axis defaultYAxis() {
        Axis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);

        return yAxis;
    }

    public ExpenditureLineChart(List<Transaction> data) {
        this(defaultXAxis(),defaultYAxis());
        this.updateData(data);
    }

    public ExpenditureLineChart(@NamedArg("xAxis") Axis<Long> xAxis, @NamedArg("yAxis") Axis<Double> yAxis) {
        super(xAxis, yAxis);

        this.getXAxis().setLabel("Month");
        this.getYAxis().setLabel("Net Expenditure");
        this.getXAxis().setAutoRanging(true);
        this.getYAxis().setAutoRanging(true);

        this.setMaxHeight(Double.MAX_VALUE);
        this.setMaxWidth(Double.MAX_VALUE);
    }

    public void updateData(List<Transaction> transactionList) {
        Map<Long, Double> dataMap = new HashMap<>();

        transactionList.sort(Comparator.comparing(Transaction::getDate));

        for(Transaction transaction: transactionList) {
            double amount = transaction.getAmount() / 100.0;

            if(amount > 0)
                continue;

            Date date = transaction.getDate();

            long mills = date.toInstant().toEpochMilli();

            if(dataMap.containsKey(mills)) {
                dataMap.put(mills, dataMap.get(mills) + amount);
            } else {
                dataMap.put(mills, amount);
            }
        }

        XYChart.Series series = new XYChart.Series();
        for (long time : dataMap.keySet()) {
            series.getData().add(new XYChart.Data(time, dataMap.get(time)));
        }
        series.setName("Change in Account Balance");
        this.getData().setAll(series);
    }
}
