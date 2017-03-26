package ledger.user_interface.ui_controllers.component.charts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chart that shows all expenditures in the form of a pie chart.
 */
public class ExpenditurePayeePieChart extends PieChart implements IUIController, IChart{

    public ExpenditurePayeePieChart(List<Transaction> data) {
        this.setMaxHeight(Double.MAX_VALUE);
        this.setMaxWidth(Double.MAX_VALUE);

        updateData(data);
    }

    public void updateData(List<Transaction> transactions) {
        Map<String, Long> tagNameToAmountSpent = new HashMap<>();
        for (Transaction t : transactions) {
            populateMap(tagNameToAmountSpent, t.getPayee().toString(), t.getAmount());
        }
        List<Data> dataList = new ArrayList<>();
        for (String tag : tagNameToAmountSpent.keySet()) {
            // use absolute value here so it's not negative
            double amountSpent = Math.abs(tagNameToAmountSpent.get(tag)) / 100;
            NumberFormat formatter = new DecimalFormat("#0.00");
            dataList.add(new Data(tag + " - " + "(" + formatter.format(amountSpent) + ")", amountSpent));
        }
        if(dataList.isEmpty()) {
            dataList.add(new Data( "No Expenses", 0));
        }

        ObservableList<Data> pieChartData = FXCollections.observableArrayList(dataList);
        this.setData(pieChartData);
        this.setTitle("Expenditures by Payee");
    }

    /**
     * Populates the map passed in with given key and value
     *
     * @param map   map in which the data is organized
     * @param key   map key to check value
     * @param value value to add to existing value or empty map
     */
    private void populateMap(Map map, String key, Long value) {
        if (!map.keySet().contains(key)) {
            map.put(key, value);
        } else {
            Long existingAmount = (Long) map.get(key);
            existingAmount += value;

            map.put(key, existingAmount);
        }
    }
}
