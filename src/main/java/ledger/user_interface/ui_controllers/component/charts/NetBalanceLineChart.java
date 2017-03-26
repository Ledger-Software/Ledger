package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import ledger.database.entity.Transaction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by CJ on 3/26/2017.
 */
public class NetBalanceLineChart extends LineChart<Long, Double> implements IChart {

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

    public NetBalanceLineChart(List<Transaction> transactionList) {
        this(defaultXAxis(),defaultYAxis());
        this.updateData(transactionList);
    }

    public NetBalanceLineChart(@NamedArg("xAxis") Axis<Long> stringAxis, @NamedArg("yAxis") Axis<Double> doubleAxis) {
        super(stringAxis, doubleAxis);
    }

    @Override
    public void updateData(List<Transaction> transactionList) {
        Map<Long, Double> dataMap = new HashMap<>();

        transactionList.sort(Comparator.comparing(Transaction::getDate));

        double runningTotal = 0;
        for(Transaction transaction: transactionList) {
            runningTotal += transaction.getAmount() / 100.0;

            Date date = transaction.getDate();

            long mills = date.toInstant().toEpochMilli();

            if(dataMap.containsKey(mills)) {
                dataMap.put(mills, dataMap.get(mills) + runningTotal);
            } else {
                dataMap.put(mills, runningTotal);
            }
        }

        XYChart.Series series = new XYChart.Series();
        for (long time : dataMap.keySet()) {
            series.getData().add(new XYChart.Data(time, dataMap.get(time)));
        }
        series.setName("Net Balance");
        this.getData().setAll(series);
    }
}
