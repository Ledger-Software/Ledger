package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.*;
import ledger.exception.StorageException;
import ledger.io.input.TypeConversion;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.tablecolumn.*;
import ledger.user_interface.utils.*;

import java.net.URL;
import java.util.*;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class TransactionTableView extends TableView<Transaction> implements IUIController, Initializable {

    private final static String pageLoc = "/fxml_files/TransactionTableView.fxml";

    @FXML
    private AmountColumn amountColumn;

    @FXML
    private AmountDebitColumn amountDebitColumn;

    @FXML
    private AmountCreditColumn amountCreditColumn;

    @FXML
    private AccountColumn accountColumn;

    @FXML
    private CheckNumberColumn checkNumberColumn;

    @FXML
    private TagColumn tagColumn;

    @FXML
    private RunningBalanceColumn runningBalanceColumn;

    private Account accountFilter;
    private String searchFilterString = "";

    public TransactionTableView() {
        this.initController(pageLoc, this, "Error on main page startup: ");
    }

    private void asyncTableUpdate() {
        Startup.INSTANCE.runLater(this::updateTransactionTableView);
    }

    private void configureTransactionTableView() {
        // Add ability to delete transactions form tableView
        this.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.DELETE) {
                handleDeleteSelectedTransactionsFromTableView();
            }
        });

        // Allow multiple row selection
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Initially show amount column & hide debit/credit columns
        this.amountColumn.setVisible(true);
        this.amountDebitColumn.setVisible(false);
        this.amountCreditColumn.setVisible(false);

        this.setUpContextMenu();
    }

    private void setUpContextMenu() {
        // Configure right-click context menu
        ContextMenu menu = new ContextMenu();

        MenuItem deleteTransactionsMenuItem = new MenuItem("Delete Selected Transaction(s)");
        menu.getItems().add(deleteTransactionsMenuItem);
        deleteTransactionsMenuItem.setOnAction(event -> handleDeleteSelectedTransactionsFromTableView());

        // Upon initialization, Tag column is displayed
        MenuItem showHideTagColumnMenuItem = new MenuItem("Hide Tag Column");
        menu.getItems().add(showHideTagColumnMenuItem);
        showHideTagColumnMenuItem.setOnAction(event -> {
            this.tagColumn.setVisible(!this.tagColumn.isVisible());
            if (this.tagColumn.isVisible()) {
                showHideTagColumnMenuItem.setText("Hide Tag Column");
            } else {
                showHideTagColumnMenuItem.setText("Show Tag Column");
            }
            updateTransactionTableView();
        });

        // Upon initialization, Account column is displayed
        MenuItem showHideAccountColumnMenuItem = new MenuItem("Hide Account Column");
        menu.getItems().add(showHideAccountColumnMenuItem);
        showHideAccountColumnMenuItem.setOnAction(event -> {
            this.accountColumn.setVisible(!this.accountColumn.isVisible());
            if (this.accountColumn.isVisible()) {
                showHideAccountColumnMenuItem.setText("Hide Account Column");
            } else {
                showHideAccountColumnMenuItem.setText("Show Account Column");
            }
            updateTransactionTableView();
        });

        // Upon initialization, Check Number column is displayed
        MenuItem showHideCheckNumberColumnMenuItem = new MenuItem("Hide Check Number Column");
        menu.getItems().add(showHideCheckNumberColumnMenuItem);
        showHideCheckNumberColumnMenuItem.setOnAction(event -> {
            this.checkNumberColumn.setVisible(!this.checkNumberColumn.isVisible());
            if (this.checkNumberColumn.isVisible()) {
                showHideCheckNumberColumnMenuItem.setText("Hide Check Number Column");
            } else {
                showHideCheckNumberColumnMenuItem.setText("Show Check Number Column");
            }
            updateTransactionTableView();
        });

        // Upon initialization, Running Balance column is displayed
        MenuItem showHideRunningBalanceColumnMenuItem = new MenuItem("Hide Running Account Balance Column");
        menu.getItems().add(showHideRunningBalanceColumnMenuItem);
        showHideRunningBalanceColumnMenuItem.setOnAction(event -> {
            this.runningBalanceColumn.setVisible(!this.runningBalanceColumn.isVisible());
            if (this.runningBalanceColumn.isVisible()) {
                showHideRunningBalanceColumnMenuItem.setText("Hide Running Account Balance Column");
            } else {
                showHideRunningBalanceColumnMenuItem.setText("Show Running Account Balance Column");
            }
            updateTransactionTableView();
        });

        // Upon initialization, Debit/Credit perspective is disabled
        MenuItem toggleDebitCreditView = new MenuItem("Enable Debit/Credit Perspective");
        menu.getItems().add(toggleDebitCreditView);
        toggleDebitCreditView.setOnAction(event -> {
            this.amountColumn.setVisible(!this.amountColumn.isVisible());
            this.amountDebitColumn.setVisible(!this.amountDebitColumn.isVisible());
            this.amountCreditColumn.setVisible(!this.amountCreditColumn.isVisible());

            if (this.amountColumn.isVisible()) {
                toggleDebitCreditView.setText("Enable Debit/Credit Perspective");
            } else {
                toggleDebitCreditView.setText("Disable Debit/Credit Perspective");
            }
            updateTransactionTableView();
        });

        MenuItem addTransactionMenuItem = new MenuItem("Add Transaction");
        menu.getItems().add(addTransactionMenuItem);
        addTransactionMenuItem.setOnAction(event -> {
            TaskWithReturn<List<Account>> accountTask = DbController.INSTANCE.getAllAccounts();
            accountTask.startTask();
            List<Account> accountList = accountTask.waitForResult();
            if (accountList.isEmpty()) {
                this.setupErrorPopup("Please create an account before adding transactions.");
                return;
            }
            Account acc = accountList.get(0);

            TaskNoReturn task = DbController.INSTANCE.insertTransaction(new Transaction(new Date(), TypeConversion.convert("UNKNOWN"), 0, acc, new Payee("", ""), true, new ArrayList<>(), new Note("")));
            task.startTask();
        });

        MenuItem undoMenuItem = new MenuItem("Undo");
        menu.getItems().add(undoMenuItem);
        undoMenuItem.setOnAction(event -> undo());

        this.setContextMenu(menu);
    }

    public void updateTransactionTableView() {
        // Update table rows
        try {
            List<Transaction> transactions;
            if (accountFilter == null || accountFilter.getName().equals("All Accounts")) {
                transactions = TransactionRunningAccountBalanceCalculator.getAllTransactionsWithRunningBalances();
            } else {
                transactions = TransactionRunningAccountBalanceCalculator.getTransactionsWithRunningBalancesForAccount(accountFilter);
            }

            // 0. Manually sort the list based on Date to force a default sorted order
            transactions.sort(Comparator.comparing(Transaction::getDate));

            ObservableList<Transaction> observableTransactions = FXCollections.observableList(transactions);

            // 1. Wrap the ObservableList in a FilteredList (initially display all data).
            FilteredList<Transaction> filteredData = new FilteredList<>(observableTransactions, p -> true);

            // 2. Set the filter Predicate.
            filteredData.setPredicate(transaction -> {
                // If filter text is empty, display all persons.
                if (searchFilterString == null || searchFilterString.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = searchFilterString.toLowerCase();

                // NOTE: Currently, the only two columns not filtered on are the Date & Cleared columns
                AmountStringConverter asc = new AmountStringConverter();
                AmountDebitStringConverter adsc = new AmountDebitStringConverter();
                AmountCreditStringConverter acsc = new AmountCreditStringConverter();
                CheckNumberStringConverter cnsc = new CheckNumberStringConverter();
                if (this.amountColumn.isVisible() && asc.toString(transaction.getAmount()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Amount column visible & filter matches standard amount display
                } else if (this.amountDebitColumn.isVisible() && adsc.toString(transaction.getAmount()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Debit column visible & filter matches debit amount display
                } else if (this.amountCreditColumn.isVisible() && acsc.toString(transaction.getAmount()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Credit column visible & filter matches credit amount display
                } else if (transaction.getPayee().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches payee name (Payee column always visible)
                } else if (this.tagColumn.isVisible() && transaction.getTags().stream().map(Tag::getName).anyMatch(s -> s.toLowerCase().contains(lowerCaseFilter))) {
                    return true; // Tag column visible & filter matches tags
                } else if (this.accountColumn.isVisible() && transaction.getAccount().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Account column visible & filter matches account name
                } else if (transaction.getType().getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches type name (Type column always visible)
                } else if (this.checkNumberColumn.isVisible() && cnsc.toString(transaction.getCheckNumber()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Check number column visible && filter matches check number
                } else if (this.runningBalanceColumn.isVisible() && asc.toString(transaction.getRunningBalance()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Running balance column visible & filter matches running balance
                } else {
                    return false; // Filter doesn't match
                }
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<Transaction> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(this.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            this.setItems(sortedData);
        } catch (StorageException e) {
            this.setupErrorPopup(e.getMessage(), e);
        }
    }

    private void handleDeleteSelectedTransactionsFromTableView() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete Transaction(s)");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you would like to delete the selected transaction(s)?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<Integer> indices = new ArrayList<>();
            // Add indices to new list so they aren't observable
            indices.addAll(this.getSelectionModel().getSelectedIndices());
            if (indices.size() != 0) {

                //TODO: Get around this scary mess
                if (indices.contains(-1)) {
                    indices = this.getSelectionModel().getSelectedIndices();
                }

                for (int i : indices) {
                    Transaction transactionToDelete = this.getItems().get(i);

                    TaskNoReturn task = DbController.INSTANCE.deleteTransaction(transactionToDelete);
                    task.RegisterFailureEvent((e) -> {
                        asyncTableUpdate();
                        setupErrorPopup("Error deleting transaction.", e);
                    });
                    task.startTask();
                    task.waitForComplete();
                }
                updateTransactionTableView();
            }
        }
    }

    /**
     * Verifies that the user would like to undo the most recent operation. If so, undoes it
     */
    public void undo() {
        String topMessage = DbController.INSTANCE.undoPeekMessage();

        if (topMessage == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Undo Not Available");
            alert.setHeaderText("There are no recent operations to undo.");
            alert.show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Undo");
        alert.setHeaderText("Do you wish to undo the follow action?");
        alert.setContentText(topMessage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DbController.INSTANCE.undo();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate listView w/ transactions from DB
        configureTransactionTableView();
        updateTransactionTableView();

        DbController.INSTANCE.registerTransactionSuccessEvent((ignored) -> this.asyncTableUpdate());
        DbController.INSTANCE.registerPayeeSuccessEvent((ignored) -> this.asyncTableUpdate());
    }

    public void updateAccountFilter(Account accountToFilterBy) {
        this.accountFilter = accountToFilterBy;
        this.asyncTableUpdate();
    }

    public void updateSearchFilterString(String searchFilterString) {
        this.searchFilterString = searchFilterString;
        this.asyncTableUpdate();
    }
}
