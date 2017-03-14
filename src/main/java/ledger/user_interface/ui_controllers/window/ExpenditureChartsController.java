package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AccountDropdown;
import ledger.user_interface.ui_controllers.component.charts.ExpenditureLineChart;
import ledger.user_interface.ui_controllers.component.charts.ExpenditurePieChart;
import ledger.user_interface.ui_controllers.component.charts.IChart;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Controls how the charts render with user given information.
 */
public class ExpenditureChartsController extends GridPane implements Initializable, IUIController {
    private final static String pageLoc = "/fxml_files/ExpenditureCharts.fxml";

    @FXML
    private AccountDropdown accountFilterDropdown;
    @FXML
    private DatePicker fromDateFilter;
    @FXML
    private DatePicker toDateFilter;
    @FXML
    private Button filterEnterButton;
    @FXML
    private HBox chartHBox;

    @FXML
    private CheckBox expenditureLineChartCheckBox;
    @FXML
    private CheckBox expenditurePieChartCheckBox;

    private List<Transaction> allTransactions;
    private List<Transaction> filteredTransactions;
    private Account accountFilter;
    private Date dateFromFilter;
    private Date dateToFilter;


    ExpenditureChartsController() {
        dateFromFilter = new Date(Long.MIN_VALUE);
        dateToFilter = new Date(Long.MAX_VALUE);
        this.initController(pageLoc, this, "Error on expenditure chart page startup: ");

        new Date(Long.MAX_VALUE);
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
        updateTransactions();
        updateCharts(this.allTransactions);

        this.filterEnterButton.setOnAction((event) -> {
            LocalDate tempLocalFromDate = this.fromDateFilter.getValue();
            LocalDate tempLocalToDate = this.toDateFilter.getValue();

            Date tempFromDate, tempToDate;

            if(tempLocalFromDate == null) {
                tempFromDate = new Date(Long.MIN_VALUE);
            } else {
                tempFromDate = Date.from(tempLocalFromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if(tempLocalToDate == null) {
                tempToDate = new Date(Long.MAX_VALUE);
            } else {
                tempToDate = Date.from(tempLocalToDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if (tempFromDate.after(tempToDate)) {
                this.setupErrorPopup("Ensure your dates are in chronological order!", new Exception());
                return;
            }

            this.dateFromFilter = tempFromDate;
            this.dateToFilter = tempToDate;
            accountFilter = this.accountFilterDropdown.getSelectedAccount();
            updateFilter();
        });

        expenditureLineChartCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                Node chart = new ExpenditureLineChart(this.filteredTransactions);
                chartHBox.getChildren().add(chart);
                HBox.setHgrow(chart, Priority.ALWAYS);
            } else {
                chartHBox.getChildren().removeIf(item -> item instanceof ExpenditureLineChart);
            }
        });

        expenditurePieChartCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                Node chart = new ExpenditurePieChart(this.filteredTransactions);
                chartHBox.getChildren().add(new ExpenditurePieChart(this.filteredTransactions));
                HBox.setHgrow(chart, Priority.ALWAYS);
            } else {
                chartHBox.getChildren().removeIf(item -> item instanceof ExpenditurePieChart
                );
            }
        });

        DbController.INSTANCE.registerTransactionSuccessEvent((ignore) -> this.updateTransactions());
        updateFilter();
    }

    private void updateFilter() {
        this.filteredTransactions = new ArrayList<>(allTransactions);

        filterBasedOnDateRange(this.dateFromFilter, this.dateToFilter);
        filterBasedOnAccount(this.accountFilter);

        updateCharts(this.filteredTransactions);
    }

    /**
     * Retrieves transactions from the database and sets field for use
     */
    private void updateTransactions() {
        TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
        task.RegisterFailureEvent(e -> setupErrorPopup("Error retrieving transactions.", new Exception()));
        task.startTask();
        this.allTransactions = task.waitForResult();
        updateFilter();
    }

    /**
     * Creates the stacked bar chart based on transactions in a specified account
     *
     * @param account the account to filter the transactions by
     */
    private void filterBasedOnAccount(Account account) {
        if(account == null)
            return;

        Iterator<Transaction> iterator = filteredTransactions.iterator();
        while(iterator.hasNext()) {
            Transaction t = iterator.next();

            if(!t.getAccount().equals(account)) {
                iterator.remove();
            }
        }
    }

    /**
     * Creates the stacked bar chart based on transactions in a specified date range
     *
     * @param from starting date to filter transactions by
     * @param to   ending date to filter transactions by
     */
    private void filterBasedOnDateRange(Date from, Date to) {
        Iterator<Transaction> iterator = filteredTransactions.iterator();
        while(iterator.hasNext()) {
            Transaction t = iterator.next();

            Date date = t.getDate();
            if(date.before(from)) {
                iterator.remove();
                continue;
            }
            if(date.after(to)) {
                iterator.remove();
                continue;
            }
        }
    }

    private void updateCharts(List<Transaction> transactionList) {
        for(Node node :chartHBox.getChildren()) {
            if(!(node instanceof IChart)) continue;

            ((IChart) node).updateData(transactionList);
        }
    }
}
