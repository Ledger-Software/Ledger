package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
    private Label displayLabel;
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
            Account accountSelected = accountFilterDropdown.getSelectedAccount();
            LocalDate fromDateSelected = fromDateFilter.getValue();
            LocalDate toDateSelected = toDateFilter.getValue();
            if (accountSelected == null && fromDateSelected == null && toDateSelected == null) {
                this.setupErrorPopup("You must either select an account or a date range to continue!", new Exception());
                return;
            }
            if ((accountSelected != null) && ((fromDateSelected == null) || (toDateSelected == null))) {
                createBasedOnAccount(accountSelected);
            }
            if ((accountSelected == null) && (fromDateSelected != null) && (toDateSelected != null)) {
                if (fromDateSelected.isAfter(toDateSelected)) {
                    this.setupErrorPopup("Ensure your dates are in chronological order!", new Exception());
                    return;
                }
                createBasedOnDateRange(fromDateSelected, toDateSelected);
            }
            if ((accountSelected != null) && (fromDateSelected != null) && (toDateSelected != null)) {
                if (fromDateSelected.isAfter(toDateSelected)) {
                    this.setupErrorPopup("Ensure your dates are in chronological order!", new Exception());
                    return;
                }
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
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount Spent");
        this.expendituresLineChart = new LineChart<>(xAxis, yAxis);
        this.expendituresLineChart.getXAxis().setAutoRanging(true);
        this.expendituresLineChart.getYAxis().setAutoRanging(true);

        Map<String, Integer> monthToAmountSpent = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            cal.setTime(t.getDate());
            String month = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
            addToMap(monthToAmountSpent, month, t.getAmount());
        }

        XYChart.Series series = new XYChart.Series();
        for (String key : monthToAmountSpent.keySet()) {
            series.getData().add(new XYChart.Data(key, monthToAmountSpent.get(key) / 100));
        }
        this.expendituresLineChart.getData().add(series);
        // TODO get to show up
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
     * @param toDate ending date to filter transactions by
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
     * @param account the account to filter the transactions by
     * @param fromDate starting date to filter transactions by
     * @param toDate ending date to filter transactions by
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
        Map<Tag, Integer> tagToAmountSpent = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            for (Tag tag : t.getTagList()) {
                addToMap(tagToAmountSpent, tag, t.getAmount());
            }
        }

        List<PieChart.Data> dataList = new ArrayList<>();
        for (Tag t : tagToAmountSpent.keySet()) {
            dataList.add(new PieChart.Data(t.getName(), tagToAmountSpent.get(t) / 100));
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(dataList);
        this.expendituresPieChart = new PieChart(pieChartData);
        //TODO get pie chart to actually show up
    }

    /**
     * Used to fill map with filtered data to show on expenditure charts
     *
     * @param map map in which the data is organized
     * @param key map key to check value
     * @param value value to add to existing value or empty map
     */
    private void addToMap(Map map, Object key, Object value) {
        if (!map.keySet().contains(key)) {
            map.put(key, value);
        } else {
            Integer existingAmount = (Integer) map.get(key);
            Integer newAmount = existingAmount + (Integer) value;
            map.put(key, newAmount);
        }
    }
}
