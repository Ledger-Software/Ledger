package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;

import java.awt.*;
import java.awt.Color;
import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

import static java.awt.Color.red;

/**
 * Controls how the charts render with user given information.
 */
public class ExpenditureChartsController extends GridPane implements Initializable, IUIController {

    @FXML
    private AccountDropdown accountFilterDropdown;
    @FXML
    private DatePicker fromDateFilter;
    @FXML
    private DatePicker toDateFilter;
    @FXML
    private PieChart expendituresPieChart;
    @FXML
    private Label currentlyShowingLabel;
    @FXML
    private LineChart expendituresLineChart;
    @FXML
    private Button filterEnterButton;

    private List<Transaction> allTransactions;
    private final static String pageLoc = "/fxml_files/ExpenditureCharts.fxml";

    ExpenditureChartsController() {
        this.initController(pageLoc, this, "Error on expenditure chart page startup: ");
        getTransactions();
        setupExpenditureHistoryChart();
        setupInitialPieChart();
    }

    /**
     * Is used to set up the charts on this page
     * <p>
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation The location used to resolve relative paths for the root object, or
     *                         <tt>null</tt> if the location is not known.
     * @param resources        The resources used to localize the root object, or <tt>null</tt> if
     *                         the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        this.filterEnterButton.setOnAction((event) -> {
            Account accountSelected = this.accountFilterDropdown.getSelectedAccount();
            LocalDate fromDateSelected = this.fromDateFilter.getValue();
            LocalDate toDateSelected = this.toDateFilter.getValue();
            if (accountSelected == null && fromDateSelected == null && toDateSelected == null) {
                this.setupErrorPopup("You must either select an account or a date range to continue!", new Exception());
                return;
            }
            if ((accountSelected != null) && ((fromDateSelected == null) || (toDateSelected == null))) {
                this.currentlyShowingLabel.setText("Currently showing expenditures for account: " + accountSelected.getName());
                createBasedOnAccount(accountSelected);
            }
            if ((accountSelected == null) && (fromDateSelected != null) && (toDateSelected != null)) {
                if (fromDateSelected.isAfter(toDateSelected)) {
                    this.setupErrorPopup("Ensure your dates are in chronological order!", new Exception());
                    return;
                }
                this.currentlyShowingLabel.setText("Currently showing expenditures for the above date range.");
                createBasedOnDateRange(fromDateSelected, toDateSelected);
            }
            if ((accountSelected != null) && (fromDateSelected != null) && (toDateSelected != null)) {
                if (fromDateSelected.isAfter(toDateSelected)) {
                    this.setupErrorPopup("Ensure your dates are in chronological order!", new Exception());
                    return;
                }
                this.currentlyShowingLabel.setText("Currently showing expenditures for account: " + accountSelected.getName() + " within the above date range.");
                createBasedOnAccountAndDateRange(accountSelected, fromDateSelected, toDateSelected);
            }
        });
    }

    /**
     * Retrieves transactions from the database and sets field for use
     */
    private void getTransactions() {
        TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
        task.RegisterFailureEvent((e) -> {
            setupErrorPopup("Error retrieving transactions.", new Exception());
        });
        task.startTask();
        this.allTransactions = task.waitForResult();
    }

    /**
     * Builds the line chart to show trends in amount spent over the last six months
     */
    private void setupExpenditureHistoryChart() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = cal.getTime();
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : this.allTransactions) {
            if ((t.getDate().before(today) || t.getDate().equals(today))
                    && (t.getDate().after(sixMonthsAgo) || t.getDate().equals(sixMonthsAgo))) {
                filteredTransactions.add(t);
            }
        }

        this.expendituresLineChart.getXAxis().setLabel("Month");
        this.expendituresLineChart.getYAxis().setLabel("Net Expenditure");
        this.expendituresLineChart.getXAxis().setAutoRanging(true);
        this.expendituresLineChart.getYAxis().setAutoRanging(true);

        Map<String, Integer> monthToAmountSpent = new HashMap<>();
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
        this.expendituresLineChart.getData().add(series);
        this.expendituresLineChart.setVisible(true);
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
            Date s1 = null;
            Date s2 = null;
            try {
                s1 = s.parse(o1);
                s2 = s.parse(o2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return s1.compareTo(s2);
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
        for (int i = 0; i < preorderedMonths.size(); i++) {
            if (monthToYear.get(preorderedMonths.get(i)) > lowestYear) {
                monthsInNextYear.add(preorderedMonths.get(i));
            }
        }
        // takes the months in the next year out of the beginning of the list and tacks them on the end
        preorderedMonths.removeAll(monthsInNextYear);
        preorderedMonths.addAll(monthsInNextYear);
    }

    /**
     * Sets up PieChart seen on initialization of expenditures screen. Data is loaded from all accounts.
     */
    private void setupInitialPieChart() {
        createPieChart(this.allTransactions);
    }

    /**
     * Creates the stacked bar chart based on transactions in a specified account
     *
     * @param account the account to filter the transactions by
     */
    private void createBasedOnAccount(Account account) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : this.allTransactions) {
            if (t.getAccount().equals(account)) {
                filteredTransactions.add(t);
            }
        }

        createPieChart(filteredTransactions);
    }

    /**
     * Creates the stacked bar chart based on transactions in a specified date range
     *
     * @param fromDate starting date to filter transactions by
     * @param toDate   ending date to filter transactions by
     */
    private void createBasedOnDateRange(LocalDate fromDate, LocalDate toDate) {
        Date from = Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : this.allTransactions) {
            if ((t.getDate().before(to) || t.getDate().equals(to))
                    && (t.getDate().after(from) || t.getDate().equals(from))) {
                filteredTransactions.add(t);
            }
        }

        createPieChart(filteredTransactions);
    }

    /**
     * Creates the stacked bar chart based the transactions that exist in a specified account within a given date range
     *
     * @param account  the account to filter the transactions by
     * @param fromDate starting date to filter transactions by
     * @param toDate   ending date to filter transactions by
     */
    private void createBasedOnAccountAndDateRange(Account account, LocalDate fromDate, LocalDate toDate) {
        List<Transaction> filteredByAccountTransactions = new ArrayList<>();
        for (Transaction t : this.allTransactions) {
            if (t.getAccount().equals(account)) {
                filteredByAccountTransactions.add(t);
            }
        }

        Date from = Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : filteredByAccountTransactions) {
            if ((t.getDate().before(to) || t.getDate().equals(to))
                    && (t.getDate().after(from) || t.getDate().equals(from))) {
                filteredTransactions.add(t);
            }
        }

        createPieChart(filteredTransactions);
    }

    /**
     * Creates a map of tag to amount spent on that tag, and then uses the map to create a Pie chart
     *
     * @param filteredTransactions transactions fitting the criteria of the filter
     */
    private void createPieChart(List<Transaction> filteredTransactions) {
        Map<String, Integer> tagNameToAmountSpent = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            if (t.getTagList().isEmpty()) {
                addToMapForPieChart(tagNameToAmountSpent, "Uncategorized", t.getAmount());
            } else {
                for (Tag tag : t.getTagList()) {
                    addToMapForPieChart(tagNameToAmountSpent, tag.getName(), t.getAmount());
                }
            }
        }
        List<PieChart.Data> dataList = new ArrayList<>();
        for (String tag : tagNameToAmountSpent.keySet()) {
            // use absolute value here so it's not negative
            double amountSpent = Math.abs(tagNameToAmountSpent.get(tag)) / 100;
            NumberFormat formatter = new DecimalFormat("#0.00");
            dataList.add(new PieChart.Data(tag + " - " + "($" + formatter.format(amountSpent) + ")", amountSpent));
        }
        if (dataList.isEmpty()) {
            this.setupErrorPopup("There's no data to be displayed!", new Exception());
            return;
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(dataList);
        this.expendituresPieChart.setData(pieChartData);
        this.expendituresPieChart.legendVisibleProperty().set(false);
        this.expendituresPieChart.setTitle("Expenditures by Category");
        this.expendituresPieChart.setVisible(true);
        int i = 0;
        List<String> pieColors = Arrays.asList("red", "orange", "green", "blue", "purple", "pink", "yellow");
        for (PieChart.Data data : dataList) {
            data.getNode().setStyle("-fx-pie-color: " + pieColors.get(i % pieColors.size()) + ";");
            i++;
        }
    }

    /**
     * Used to determine what filtered data should show on expenditure pie chart
     * Only adds up amount spent (minus dollars) on items - doesn't consider deposits
     *
     * @param map   map in which the data is organized
     * @param key   map key to check value
     * @param value value to add to existing value or empty map
     */
    private void addToMapForPieChart(Map map, String key, Integer value) {
        if (value <= 0) {
            if (key.equals("")) {
                key = "Uncategorized";
            }
            populateMap(map, key, value);
        }
    }

    /**
     * Used to determine what filtered data should show on expenditure line chart
     * Adds up amount spent and considers deposits for a net account balance change
     *
     * @param map   map in which the data is organized
     * @param key   map key to check value
     * @param value value to add to existing value or empty map
     */
    private void addToMapForLineChart(Map map, String key, Integer value) {
        populateMap(map, key, value);
    }

    /**
     * Populates the map passed in with given key and value
     *
     * @param map   map in which the data is organized
     * @param key   map key to check value
     * @param value value to add to existing value or empty map
     */
    private void populateMap(Map map, String key, Integer value) {
        if (!map.keySet().contains(key)) {
            map.put(key, value);
        } else {
            Integer existingAmount = (Integer) map.get(key);
            Integer newAmount = existingAmount;
            newAmount += value;

            map.put(key, newAmount);
        }
    }
}
