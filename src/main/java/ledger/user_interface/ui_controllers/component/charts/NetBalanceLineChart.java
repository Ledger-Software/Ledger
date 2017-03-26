package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import ledger.database.entity.Transaction;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CJ on 3/26/2017.
 */
public class NetBalanceLineChart extends LineChart<String, Double> implements IChart {

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

    public NetBalanceLineChart(List<Transaction> transactionList) {
        this(defaultXAxis(),defaultYAxis());
        this.updateData(transactionList);
    }

    public NetBalanceLineChart(@NamedArg("xAxis") Axis<String> stringAxis, @NamedArg("yAxis") Axis<Double> doubleAxis) {
        super(stringAxis, doubleAxis);
    }

    @Override
    public void updateData(List<Transaction> transactionList) {
        Map<String, Double> dataMap = new HashMap<>();

        for(Transaction transaction: transactionList) {
            double amount = transaction.getAmount() / 100.0;

            String month = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth().toString();

            if(dataMap.containsKey(month)) {
                dataMap.put(month, dataMap.get(month) + amount);
            } else {
                dataMap.put(month, amount);
            }
        }

        XYChart.Series series = new XYChart.Series();
        for (String m : dataMap.keySet()) {
            series.getData().add(new XYChart.Data(m, dataMap.get(m)));
        }
        series.setName("Net Balance");
        this.getData().setAll(series);
    }
}
