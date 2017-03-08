package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ledger.database.entity.Transaction;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Line Chart that shows monthly differentials
 */
public class ExpenditureLineChart extends LineChart<String,Long> {

    public ExpenditureLineChart(@NamedArg("xAxis") Axis<String> xAxis, @NamedArg("yAxis") Axis<Long> yAxis) {
        super(xAxis, yAxis);

        this.getXAxis().setLabel("Month");
        this.getYAxis().setLabel("Net Expenditure");
        this.getXAxis().setAutoRanging(true);
        this.getYAxis().setAutoRanging(true);
    }

    public void updateData(List<Transaction> transactions) {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = cal.getTime();
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if ((t.getDate().before(today) || t.getDate().equals(today))
                    && (t.getDate().after(sixMonthsAgo) || t.getDate().equals(sixMonthsAgo))) {
                filteredTransactions.add(t);
            }
        }



        Map<String, Long> monthToAmountSpent = new HashMap<>();
        Map<String, Integer> monthToYear = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            cal.setTime(t.getDate());
            String month = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
            Integer year = cal.get(Calendar.YEAR);
            monthToYear.put(month, year);
            addToMapForLineChart(monthToAmountSpent, month, t.getAmount());
        }

        Set<String> months = monthToAmountSpent.keySet();
        List<String> preorderedMonths = new ArrayList<>();
        preorderedMonths.addAll(months);

        orderMonthsAndYears(monthToYear, preorderedMonths);

        XYChart.Series series = new XYChart.Series();
        for (String m : preorderedMonths) {
            series.getData().add(new XYChart.Data(m, monthToAmountSpent.get(m) / 100));
        }
        series.setName("Change in Account Balance");
        this.getData().setAll(series);
    }

    /**
     * Takes care of ordering the months chronologically and also handles the switch
     * from December to January in a new year
     *
     * @param monthToYear      HashMap that keeps references from each month to their respective year
     * @param preorderedMonths Unordered list of months
     */
    private void orderMonthsAndYears(Map<String, Integer> monthToYear, List<String> preorderedMonths) {
        preorderedMonths.sort((String o1, String o2) -> {
            SimpleDateFormat s = new SimpleDateFormat("MMM");
            try {
                Date s1 = s.parse(o1);
                Date s2 = s.parse(o2);
                return s1.compareTo(s2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        });
        // takes care of transitioning into a new year
        List<String> monthsInNextYear = new ArrayList<>();
        Integer lowestYear = 0;
        for (int j = 0; j < preorderedMonths.size(); j++) {
            if (j == 0) {
                lowestYear = monthToYear.get(preorderedMonths.get(j));
            } else if (monthToYear.get(preorderedMonths.get(j)) < lowestYear) {
                lowestYear = monthToYear.get(preorderedMonths.get(j));
            }
        }
        for (String preorderedMonth : preorderedMonths) {
            if (monthToYear.get(preorderedMonth) > lowestYear) {
                monthsInNextYear.add(preorderedMonth);
            }
        }
        // takes the months in the next year out of the beginning of the list and tacks them on the end
        preorderedMonths.removeAll(monthsInNextYear);
        preorderedMonths.addAll(monthsInNextYear);
    }

    /**
     * Used to determine what filtered data should show on expenditure line chart
     * Adds up amount spent and considers deposits for a net account balance change
     *
     * @param map   map in which the data is organized
     * @param key   map key to check value
     * @param value value to add to existing value or empty map
     */
    private void addToMapForLineChart(Map map, String key, Long value) {
        populateMap(map, key, value);
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
