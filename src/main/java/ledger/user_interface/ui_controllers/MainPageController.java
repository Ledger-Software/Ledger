package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.exception.StorageException;
import ledger.user_interface.ui_models.TransactionModel;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class MainPageController extends GridPane implements Initializable, IUIController {
    @FXML
    private Button addAccountBtn;
    @FXML
    private Button importTransactionsBtn;
    @FXML
    private Button trackSpendingBtn;
    @FXML
    private Button addTransactionBtn;

    private final static String pageLoc = "/fxml_files/MainPage.fxml";
    // Transaction table UI objects
    @FXML
    private TableView transactionTableView;
    @FXML
    private TableColumn amountColumn;
    @FXML
    private TableColumn dateColumn;
    @FXML
    private TableColumn payeeColumn;
    @FXML
    private TableColumn typeColumn;
    @FXML
    private TableColumn categoryColumn;
    @FXML
    private TableColumn clearedColumn;

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<TransactionModel, String>> amountEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String amountToSetString = t.getNewValue();
                int dollarsToSet = Integer.parseInt(amountToSetString.substring(1, amountToSetString.length() - 3));
                int centsToSet = Integer.parseInt(amountToSetString.substring(amountToSetString.length() - 2, amountToSetString.length()));
                int amountToSet = (dollarsToSet * 100) + centsToSet;

                Transaction transaction = model.getTransaction();
                transaction.setAmount(amountToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction amount.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> dateEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String dateToSetString = t.getNewValue();

                DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                Date dateToSet = formatter.parse(dateToSetString);

                Transaction transaction = model.getTransaction();
                transaction.setDate(dateToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction date.", e);
            } catch (ParseException e) {
                setupErrorPopup("Error parsing date string.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> payeeEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String payeeNameToSet = t.getNewValue();

                TaskWithReturn<List<Payee>> payeeQuery = DbController.INSTANCE.getAllPayees();
                payeeQuery.startTask();
                List<Payee> allPayees = payeeQuery.waitForResult();

                Payee payeeToSet = new Payee(payeeNameToSet, "");
                for (Payee currentPayee : allPayees) {
                    if (currentPayee.getName().equals(payeeNameToSet)) {
                        payeeToSet = currentPayee;
                    }
                }

                Transaction transaction = model.getTransaction();
                transaction.setPayee(payeeToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction payee.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> typeEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String typeNameToSet = t.getNewValue();

                TaskWithReturn<List<Type>> typeQuery = DbController.INSTANCE.getAllTypes();
                typeQuery.startTask();
                List<Type> allTypes = typeQuery.waitForResult();

                Type typeToSet = new Type(typeNameToSet, "");
                for (Type currentType : allTypes) {
                    if (currentType.getName().equals(typeNameToSet)) {
                        typeToSet = currentType;
                    }
                }

                Transaction transaction = model.getTransaction();
                transaction.setType(typeToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction payee.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> categoryEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String tagsNamesToSet = t.getNewValue();
                String[] tagNames = tagsNamesToSet.split(", ");


                TaskWithReturn<List<Tag>> tagQuery = DbController.INSTANCE.getAllTags();
                tagQuery.startTask();
                List<Tag> allTags = tagQuery.waitForResult();

                ArrayList<Tag> tagsToSet = new ArrayList<>();
                for (int i = 0; i < tagNames.length; i++) {
                    String currentTagName = tagNames[i];
                    Tag currentTagToSet = new Tag(currentTagName, "");
                    for (Tag currentTag : allTags) {
                        if (currentTag.getName().equals(currentTagName)) {
                            currentTagToSet = currentTag;
                        }
                    }
                    tagsToSet.add(currentTagToSet);
                }

                Transaction transaction = model.getTransaction();
                transaction.setTagList(tagsToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction payee", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> closedEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                String pendingToSetString = t.getNewValue();

                boolean pendingToSet = model.getTransaction().isPending();
                if (pendingToSetString.equals("Cleared")) {
                    pendingToSet = false;
                } else if (pendingToSetString.equals("Pending")) {
                    pendingToSet = true;
                } else {
                    setupErrorPopup("Transaction pending status not updated. Invalid input - must be 'Cleared' or 'Pending'.", new NullPointerException("Invalid Input"));
                }

                Transaction transaction = model.getTransaction();
                transaction.setPending(pendingToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.startTask();
                task.waitForComplete();

                updateTransactionTableView();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction amount.", e);
            }
        }
    };


    MainPageController() {
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
        this.addAccountBtn.setOnAction((event) -> {
            createAccountPopup();
        });

        this.addTransactionBtn.setOnAction((event) -> {
            createAddTransPopup();
        });

        this.trackSpendingBtn.setOnAction((event) -> {
            createExpenditureChartsPage();
        });

        this.importTransactionsBtn.setOnAction((event) -> {
            createImportTransPopup();
        });
        // Populate listView w/ transactions from DB
        configureTransactionTableView();
        updateTransactionTableView();
    }

    private void configureTransactionTableView() {
        this.amountColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("amount"));
        this.dateColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("date"));
        this.payeeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("payeeName"));
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("typeName"));
        this.categoryColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("tagNames"));
        this.clearedColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("pending"));

        this.amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.amountColumn.setOnEditCommit(this.amountEditHandler);

        this.dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.dateColumn.setOnEditCommit(this.dateEditHandler);

        this.payeeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.payeeColumn.setOnEditCommit(this.payeeEditHandler);

        this.typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.typeColumn.setOnEditCommit(this.typeEditHandler);

        this.categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.categoryColumn.setOnEditCommit(this.categoryEditHandler);

        this.clearedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.clearedColumn.setOnEditCommit(this.closedEditHandler);

        // Add ability to delete transactions form tableView
        this.transactionTableView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                //Put your awesome application specific logic here
                if (t.getCode() == KeyCode.DELETE) {
                    handleDeleteTransactionFromTableView();
                    updateTransactionTableView();
                }
            }
        });
    }

    private void updateTransactionTableView() {
        try {
            TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
            task.startTask();
            List<Transaction> allTransactions = task.waitForResult();

            ArrayList<TransactionModel> models = new ArrayList<>();
            for (int i = 0; i < allTransactions.size(); i++) {
                TransactionModel modelToAdd = new TransactionModel(allTransactions.get(i));
                models.add(modelToAdd);
            }
            ObservableList<TransactionModel> observableTransactionModels = FXCollections.observableList(models);

            this.transactionTableView.setItems(observableTransactionModels);

        } catch (StorageException e) {
            setupErrorPopup("Error loading all transactions into list view.", e);
        }
    }

    private void handleDeleteTransactionFromTableView() {
        try {
            int selectedIndex = this.transactionTableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                TransactionModel model = (TransactionModel) this.transactionTableView.getItems().get(selectedIndex);
                Transaction transactionToDelete = model.getTransaction();

                TaskWithArgs<Transaction> task = DbController.INSTANCE.deleteTransaction(transactionToDelete);
                task.startTask();
                task.waitForComplete();
            } else {
                setupErrorPopup("No transactions deleted.", new NullPointerException("No transaction deleted."));
            }
        } catch (StorageException e) {
            setupErrorPopup("Error deleting transaction.", e);
        }
    }

    /**
     * Creates the Import Transaction modal
     */
    private void createImportTransPopup() {
        ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
        Scene scene = new Scene(importTrxnController);
        this.createModal(scene, "Import Transactions");
    }

    /**
     * Creates the expenditure chart page
     */
    private void createExpenditureChartsPage() {
        ExpenditureChartsController chartController = new ExpenditureChartsController();
        Scene scene = new Scene(chartController);
        this.createModal(scene, "Expenditure Charts");
    }

    /**
     * Creates the Add Transaction modal
     */
    private void createAddTransPopup() {
        TransactionPopupController trxnController = new TransactionPopupController();
        Scene scene = new Scene(trxnController);
        this.createModal(scene, "Add Transaction");
    }

    /**
     * Creates the Add Account modal
     */
    private void createAccountPopup() {
        AccountPopupController accountController = new AccountPopupController();
        Scene scene = new Scene(accountController);
        this.createModal(scene, "Add Account");
    }
}
