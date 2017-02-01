package ledger.user_interface.ui_controllers.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AccountDropdown;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.text.*;
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
    private Button filterEnterButton;
    @FXML
    private CheckComboBox chartTypeDropdown;
    @FXML
    private FlowPane windowPane;

    private List<Transaction> allTransactions;
    private final static String pageLoc = "/fxml_files/ExpenditureCharts.fxml";
    private PieChart expendituresPieChart = new PieChart();
    private CategoryAxis xAxis = new CategoryAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart expendituresLineChart = new LineChart(xAxis, yAxis);
    private List<String> chartTypesSelected = new ArrayList<>();
    private int numberOfChartsSelected = 0;

    ExpenditureChartsController() {
        this.initController(pageLoc, this, "Error on expenditure chart page startup: ");
        getTransactions();
        setupChartTypeDropdown();
        setupInitCharts();

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
            if (setUpChartsInfo()) {
                return;
            }
            setUpFiltersAndDelegate();
        });
    }

    private boolean setUpChartsInfo() {
        ObservableList<String> choices = this.chartTypeDropdown.getCheckModel().getCheckedItems();
        this.chartTypesSelected.clear();
        for (String s : choices) {
            this.chartTypesSelected.add(s);
        }
        this.numberOfChartsSelected = this.chartTypesSelected.size();
        if (this.numberOfChartsSelected >= 4) {
            setupErrorPopup("Please ensure that no more than 4 types of charts are selected!");
            return true;
        }
        return false;
    }

    private void setUpFiltersAndDelegate() {
        Account accountSelected = this.accountFilterDropdown.getSelectedAccount();
        LocalDate fromDateSelected = this.fromDateFilter.getValue();
        LocalDate toDateSelected = this.toDateFilter.getValue();
        this.windowPane.getChildren().clear();
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
    }

    /**
     * Sets up the options presented in the chart type chooser.
     */
    private void setupChartTypeDropdown() {
        final ObservableList<String> options = FXCollections.observableArrayList(
                "Line Chart",
                "Pie Chart"
        );
        this.chartTypeDropdown.getItems().addAll(options);
    }

    /**
     * Sets up charts seen upon initialization.
     */
    private void setupInitCharts() {
        setUpLineChartOnPaneLeft();
        setupExpenditureHistoryChart(this.allTransactions);

        setUpPieChartOnPaneRight();
        setupInitialPieChart();
    }

    /**
     * Sets up charts seen upon initialization.
     */
    private void setupDefaultSyncCharts(List<Transaction> filteredTransactions) {
        this.windowPane.getChildren().clear();
        setUpLineChartOnPaneLeft();
        setupExpenditureHistoryChart(filteredTransactions);

        setUpPieChartOnPaneRight();
        createPieChart(filteredTransactions);
    }

    /**
     * Sets up the line chart upon initialization on a pane.
     */
    private void setUpLineChartOnPaneLeft() {
        this.windowPane.getChildren().add(this.expendituresLineChart);
        this.expendituresLineChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
        this.expendituresLineChart.prefHeightProperty().bind(this.windowPane.heightProperty());
        this.windowPane.setVisible(true);
    }

    /**
     * Sets up the pie chart dynamically on a pane.
     */
    private void setUpPieChartOnPaneRight() {
        this.expendituresPieChart.setAnimated(false);
        this.windowPane.getChildren().add(this.expendituresPieChart);
        this.expendituresPieChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
        this.expendituresPieChart.prefHeightProperty().bind(this.windowPane.heightProperty());
        this.windowPane.setVisible(true);
    }

    /**
     * Retrieves transactions from the database and sets field for use
     */
    private void getTransactions() {
        TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
        task.RegisterFailureEvent(e -> {
            setupErrorPopup("Error retrieving transactions.", new Exception());
        });
        task.startTask();
        this.allTransactions = task.waitForResult();
    }

    /**
     * Builds the line chart to show trends in amount spent over the last six months
     */
    private void setupExpenditureHistoryChart(List<Transaction> transactions) {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        // Line chart only goes back a year at max for sake of clarity
        cal.add(Calendar.MONTH, -12);
        Date twelveMonthsAgo = cal.getTime();
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if ((t.getDate().before(today) || t.getDate().equals(today))
                    && (t.getDate().after(twelveMonthsAgo) || t.getDate().equals(twelveMonthsAgo))) {
                filteredTransactions.add(t);
            }
        }

        this.expendituresLineChart.getXAxis().setLabel("Month");
        this.expendituresLineChart.getYAxis().setLabel("Net Expenditure");
        this.expendituresLineChart.getXAxis().setAutoRanging(true);
        this.expendituresLineChart.getYAxis().setAutoRanging(true);
        this.expendituresLineChart.setAnimated(false);

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
        this.expendituresLineChart.getData().clear();
        this.expendituresLineChart.getData().add(series);
        setLineChartProperties();

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
        makeChart(filteredTransactions);
    }

    /**
     * Determines how the charts will appear on the window pane
     *
     * @param filteredTransactions filtered transaction list
     */
    private void makeChart(List<Transaction> filteredTransactions) {
        for (String s : this.chartTypesSelected) {
            if (s.equals("Pie Chart")) {
                createPieChart(filteredTransactions);
            }
            if (s.equals("Line Chart")) {
                setupExpenditureHistoryChart(filteredTransactions);
            }
        }
        if (this.chartTypesSelected.isEmpty()) {
            setupDefaultSyncCharts(filteredTransactions);
        }

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
        makeChart(filteredTransactions);

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

        makeChart(filteredTransactions);
    }

    /**
     * Creates a map of tag to amount spent on that tag, and then uses the map to create a Pie chart
     *
     * @param filteredTransactions transactions fitting the criteria of the filter
     */
    private void createPieChart(List<Transaction> filteredTransactions) {
        Map<String, Integer> tagNameToAmountSpent = new HashMap<>();
        for (Transaction t : filteredTransactions) {
            if (t.getTags().isEmpty()) {
                addToMapForPieChart(tagNameToAmountSpent, "Uncategorized", t.getAmount());
            } else {
                for (Tag tag : t.getTags()) {
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
        setUpPieChartProperties();
        this.expendituresPieChart.setVisible(true);
    }

    private void setUpPieChartProperties() {
        this.expendituresPieChart.setTitle("Expenditures by Category");
        if (this.windowPane.getChildren().contains(this.expendituresPieChart)) {
            this.windowPane.getChildren().remove(this.expendituresPieChart);
        }
        this.windowPane.getChildren().add(this.expendituresPieChart);
        if (this.numberOfChartsSelected > 2) {
            this.expendituresPieChart.prefHeightProperty().bind(this.windowPane.heightProperty().divide(2));
            this.expendituresPieChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
        } else if (this.numberOfChartsSelected == 0 || this.numberOfChartsSelected == 2) {
            this.expendituresPieChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
            this.expendituresPieChart.prefHeightProperty().bind(this.windowPane.heightProperty());
        } else {
            this.expendituresPieChart.prefWidthProperty().bind(this.windowPane.widthProperty());
            this.expendituresPieChart.prefHeightProperty().bind(this.windowPane.heightProperty());
        }
    }

    private void setLineChartProperties() {
        this.expendituresLineChart.setTitle("Expenditures Over Time");
        if (this.windowPane.getChildren().contains(this.expendituresLineChart)) {
            this.windowPane.getChildren().remove(this.expendituresLineChart);
        }
        this.windowPane.getChildren().add(this.expendituresLineChart);
        if (this.numberOfChartsSelected > 2) {
            this.expendituresPieChart.prefHeightProperty().bind(this.windowPane.heightProperty().divide(2));
            this.expendituresPieChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
        } else if (this.numberOfChartsSelected == 0 || this.numberOfChartsSelected == 2) {
            this.expendituresLineChart.prefWidthProperty().bind(this.windowPane.widthProperty().divide(2));
            this.expendituresLineChart.prefHeightProperty().bind(this.windowPane.heightProperty());
        } else {
            this.expendituresLineChart.prefWidthProperty().bind(this.windowPane.widthProperty());
            this.expendituresLineChart.prefHeightProperty().bind(this.windowPane.heightProperty());
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
