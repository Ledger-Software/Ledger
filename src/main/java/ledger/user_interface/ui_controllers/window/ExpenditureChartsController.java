package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AccountDropdown;
import ledger.user_interface.ui_controllers.component.charts.ExpenditureLineChart;
import ledger.user_interface.ui_controllers.component.charts.ExpenditurePieChart;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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
    private ExpenditurePieChart expendituresPieChart;
    @FXML
    private ExpenditureLineChart expendituresLineChart;
    @FXML
    private Button filterEnterButton;

    private List<Transaction> allTransactions;
    private final static String pageLoc = "/fxml_files/ExpenditureCharts.fxml";

    ExpenditureChartsController() {
        this.initController(pageLoc, this, "Error on expenditure chart page startup: ");
        getTransactions();
        expendituresLineChart.updateData(this.allTransactions);
        expendituresPieChart.updateData(this.allTransactions);
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
        task.RegisterFailureEvent(e -> setupErrorPopup("Error retrieving transactions.", new Exception()));
        task.startTask();
        this.allTransactions = task.waitForResult();
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

        expendituresPieChart.updateData(filteredTransactions);
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

        expendituresPieChart.updateData(filteredTransactions);
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

        expendituresPieChart.updateData(filteredTransactions);
    }
}
