package ledger.user_interface.ui_controllers.window;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.layout.VBox;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Frequency;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.AccountInfo;
import ledger.user_interface.ui_controllers.component.LogoutButton;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

import java.net.URL;
import java.util.*;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class MainPageController extends GridPane implements Initializable, IUIController {
    private final static String pageLoc = "/fxml_files/MainPage.fxml";
    public static MainPageController INSTANCE;

    @FXML
    private Button payeeEditorButton;
    @FXML
    private Button addAccountBtn;
    @FXML
    private Button deleteAccountBtn;
    @FXML
    private Button importTransactionsBtn;
    @FXML
    private Button trackSpendingBtn;
    @FXML
    private Button addTransactionBtn;
    @FXML
    private Button recurringTransactionBtn;
    @FXML
    private VBox accountsVBox;
    @FXML
    private ListView<AccountInfo> accountListView;
    @FXML
    private Button clearButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private LogoutButton logoutBtn;
    @FXML
    private TransactionTableView transactionTableView;
    @FXML
    private Button addIgnoreTransactionButton;

    public MainPageController() {
        INSTANCE = this;
        this.initController(pageLoc, this, "Error on main page startup: ");
    }

    /**
     * Sets up action listeners for the page, allowing for navigation
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
        this.addAccountBtn.setOnAction((event) -> createAccountPopup());

        this.deleteAccountBtn.setOnAction((event) -> deleteAccount());

        this.addTransactionBtn.setOnAction((event) -> createAddTransPopup());

        this.trackSpendingBtn.setOnAction((event) -> createExpenditureChartsPage());

        this.importTransactionsBtn.setOnAction((event) -> createImportTransPopup());

        this.clearButton.setOnAction(this::clearSearch);

        this.searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.updateSearchOnChange();
        });

        this.recurringTransactionBtn.setOnAction(this::createRecurringTransactionPopup);

        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.RegisterFailureEvent(e -> setupErrorPopup("Error retrieving accounts.", new Exception()));
        task.startTask();
        List<Account> acts = task.waitForResult();
        if (acts.isEmpty()) {
            setUpAccountCreationHelp();
        }

        List<AccountInfo> infoItems = new ArrayList<>();
        for (Account a : acts) {
            infoItems.add(new AccountInfo(a));
        }

        // Adds the All Accounts Aggregation
        AccountInfo allInfo = new AccountInfo(null);
        allInfo.setAllAccountBalance();
        infoItems.add(0, allInfo);

        ObservableList<AccountInfo> accounts = FXCollections.observableArrayList(infoItems);
        this.accountListView.setItems(accounts);
        DbController.INSTANCE.registerAccountSuccessEvent((ignored) -> Startup.INSTANCE.runLater(this::updateAccounts));

        this.accountListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.accountListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            // if item is recently deleted we want to ignore it
            if (accountListView.getSelectionModel().getSelectedItem() != null) {
                transactionTableView.updateAccountFilter(accountListView.getSelectionModel().getSelectedItem().getAccount());
            }
            accountListView.getItems().get(0).setAllAccountBalance();
        });

        this.payeeEditorButton.setOnAction(this::openAutoTaggingEditor);
        this.addIgnoreTransactionButton.setOnAction(this::openIgnoredTransactionEditor);

        this.setOnKeyPressed(event -> {
            if (!event.isControlDown())
                return;

            if (!(event.getCode() == KeyCode.Z))
                return;

            this.undo();
        });

        this.checkForRecurringTransactions();
    }

    private void checkForRecurringTransactions() {
        TaskWithReturn<List<RecurringTransaction>> recurringTransactionTask = DbController.INSTANCE.getAllRecurringTransactions();
        recurringTransactionTask.startTask();
        List<RecurringTransaction> recurringTransactions = recurringTransactionTask.waitForResult();


        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            Frequency transactionFrequency = recurringTransaction.getFrequency();
            if (transactionFrequency.equals(Frequency.Daily)) {
                Date dateNow = new Date();
                Calendar calendarNow = Calendar.getInstance();
                calendarNow.setTime(dateNow);

                Calendar startDate = recurringTransaction.getStartDate();

                if (calendarNow.before(startDate)) continue;

                continue;
            } else if (transactionFrequency.equals(Frequency.Weekly)) {

                continue;
            } else if (transactionFrequency.equals(Frequency.Monthly)) {

                continue;
            } else if (transactionFrequency.equals(Frequency.Yearly)) {

                continue;
            } else {
                System.err.println("Invalid Frequency for Recurring Transaction: " + recurringTransaction.toString());
            }
        }
    }

    private void createRecurringTransactionPopup(ActionEvent actionEvent) {
        RecurringTransactionController controller = new RecurringTransactionController();
        Scene scene = new Scene(controller);
        this.createModal(this.getScene().getWindow(), scene, "Add Recurring Transaction");
    }

    private void openIgnoredTransactionEditor(ActionEvent actionEvent) {
        IgnoredExpressionPopupController ignoredExpressionPopupController = new IgnoredExpressionPopupController();
        Scene scene = new Scene(ignoredExpressionPopupController);
        this.createModal(this.getScene().getWindow(), scene, "Automatically Ignored Transactions");
    }

    /**
     * Used to update all accounts after selecting accounts or importing/adding transactions.
     */
    public void updateAccounts() {
        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.startTask();
        List<Account> accounts = task.waitForResult();
        List<AccountInfo> infoItems = new ArrayList<>();
        for (Account a : accounts) {
            infoItems.add(new AccountInfo(a));
        }
        // Adds the All Accounts Aggregation
        Account allActs = new Account("All Accounts", "All accounts");
        AccountInfo allInfo = new AccountInfo(allActs);
        allInfo.setAllAccountBalance();
        infoItems.add(0, allInfo);
        this.accountListView.setItems(FXCollections.observableArrayList(infoItems));
        if (this.accountListView.getItems().size() == 1) {
            this.accountListView.getSelectionModel().select(0);
        }
    }

    private void undo() {
        String topMessage = DbController.INSTANCE.undoPeekMessage();

        if (topMessage == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Undo");
        alert.setHeaderText("Do you wish to undo the follow action?");
        alert.setContentText(topMessage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DbController.INSTANCE.undo();
        }
    }

    private void openAutoTaggingEditor(ActionEvent actionEvent) {
        AutoTaggingTableWindow window = new AutoTaggingTableWindow();
        Scene scene = new Scene(window);

        this.createModal(null, scene, "Automatic Tagging", true);
    }

    private void setUpAccountCreationHelp() {
        Alert a = new Alert(Alert.AlertType.NONE);
        String message = "Now that you've successfully set up a database, you must define one " +
                "or more accounts that you can import or add transactions to. Do this by clicking " +
                "the 'Add Accounts' button on the left.";
        this.createIntroductionAlerts("Welcome!", message, a);
    }

    private void clearSearch(ActionEvent actionEvent) {
        searchTextField.setText("");
        transactionTableView.updateSearchFilterString("");
    }

    private void updateSearchOnChange() {
        String searchText = searchTextField.getText();
        transactionTableView.updateSearchFilterString(searchText);
    }

    /**
     * Creates the Import Transaction modal
     */
    private void createImportTransPopup() {
        ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
        Scene scene = new Scene(importTrxnController);
        this.createModal(this.getScene().getWindow(), scene, "Import Transactions");
    }

    /**
     * Creates the expenditure chart page
     */
    private void createExpenditureChartsPage() {
        FinanceAnalysisController chartController = new FinanceAnalysisController();
        Scene scene = new Scene(chartController);
        this.createModal(this.getScene().getWindow(), scene, "Financial Analysis", true, Modality.APPLICATION_MODAL, StageStyle.DECORATED);
    }

    /**
     * Creates the Add Transaction modal
     */
    private void createAddTransPopup() {
        TransactionPopupController trxnController = new TransactionPopupController();
        Scene scene = new Scene(trxnController);
        this.createModal(this.getScene().getWindow(), scene, "Add Transaction");
    }

    /**
     * Creates the Add Account modal
     */
    private void createAccountPopup() {
        AccountPopupController accountController = new AccountPopupController();
        Scene scene = new Scene(accountController);
        this.createModal(this.getScene().getWindow(), scene, "Add Account");
    }

    /**
     * Deletes the Account selected in the chooseAccount dropdown
     */
    private void deleteAccount() {
        if (this.accountListView.getSelectionModel().getSelectedItem() == null) {

            setupErrorPopup("Cannot delete the All Accounts aggregation. Please select an account " +
                    "to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Account Delete");
        alert.setHeaderText(null);
        alert.setContentText("Deleting " + this.accountListView.getSelectionModel().getSelectedItem().getAccount().getName() + " will delete all Transactions" +
                " associated with the account. Do you wish to proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // ... user chose OK
            TaskNoReturn t = DbController.INSTANCE.deleteAccount(this.accountListView.getSelectionModel().getSelectedItem().getAccount());
            this.accountListView.getItems().remove(this.accountListView.getSelectionModel().getSelectedItem());
            t.RegisterFailureEvent(Throwable::printStackTrace);
            t.startTask();
        }
    }
}
