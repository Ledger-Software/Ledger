package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.exception.StorageException;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls all input and interaction with the Main Page of the application
 */
public class MainPageController extends GridPane implements Initializable {

    @FXML
    private Button addAccountBtn;
    @FXML
    private Button importTransactionsBtn;
    @FXML
    private Button trackSpendingBtn;
    @FXML
    private Button addTransactionBtn;

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
    private TableColumn closedColumn;

    private static String pageLoc = "/fxml_files/MainPage.fxml";

    MainPageController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on main page startup: " + e);
        }
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
            try {
                AccountPopupController accountController = new AccountPopupController();
                Scene scene = new Scene(accountController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering add account screen: " + e);
            }
        });

        this.addTransactionBtn.setOnAction((event) -> {
            try {
                TransactionPopupController trxnController = new TransactionPopupController();
                Scene scene = new Scene(trxnController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering add transaction screen: " + e);
            }
        });

        this.trackSpendingBtn.setOnAction((event) -> {
            try {
                ExpenditureChartsController chartController = new ExpenditureChartsController();
                Scene scene = new Scene(chartController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering expenditure charts screen: " + e);
            }
        });

        this.importTransactionsBtn.setOnAction((event) -> {
            try {
                ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
                Scene scene = new Scene(importTrxnController);
                Stage newStage = new Stage();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.show();
            } catch (Exception e) {
                System.out.println("Error on triggering import transactions screen: " + e);
            }
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
        this.closedColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("pending"));

        this.amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.amountColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
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
                            System.out.println("Error editing transaction amount: " + e.getMessage());
                        }
                    }
                }
        );

        this.dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.dateColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
                    @Override
                    public void handle(CellEditEvent<TransactionModel, String> t) {
                        try {
                            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                            String dateToSetString = t.getNewValue();

//                            LocalDateTime ldt = LocalDateTime.parse(dateToSetString);
//                            Date dateToSet = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            Date dateToSet = formatter.parse(dateToSetString);

                            Transaction transaction = model.getTransaction();
                            transaction.setDate(dateToSet);

                            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                            task.startTask();
                            task.waitForComplete();

                            updateTransactionTableView();
                        } catch (StorageException e) {
                            System.out.println("Error editing transaction date: " + e.getMessage());
                        } catch (ParseException e) {
                            System.out.println("Error parsing date string: " + e.getMessage());
                        }
                    }
                }
        );

        this.payeeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.payeeColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
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
                            System.out.println("Error editing transaction payee: " + e.getMessage());
                        }
                    }
                }
        );

        this.typeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.typeColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
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
                            System.out.println("Error editing transaction payee: " + e.getMessage());
                        }
                    }
                }
        );

        this.categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.categoryColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
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
                            System.out.println("Error editing transaction payee: " + e.getMessage());
                        }
                    }
                }
        );

        this.closedColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.closedColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<TransactionModel, String>>() {
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
                                System.out.print("Transaction pending status not updated. Invalid input.");
                            }

                            Transaction transaction = model.getTransaction();
                            transaction.setPending(pendingToSet);

                            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                            task.startTask();
                            task.waitForComplete();

                            updateTransactionTableView();
                        } catch (StorageException e) {
                            System.out.println("Error editing transaction amount: " + e.getMessage());
                        }
                    }
                }
        );

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
            System.out.println("Error of loading all transactions into list view: " + e.getMessage());
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
                System.out.println("No transactions deleted. Selected index: " + selectedIndex);
            }
        } catch (StorageException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
        }
    }

    public class TransactionModel {
        private int id;
        private String amount;
        private String date;
        private String payeeName;
        private String typeName;
        private String tagNames;
        private String pending;
        private Transaction transaction;

        public TransactionModel(Transaction transaction) {
            this.id = transaction.getId();
            String amountInCents = String.valueOf(transaction.getAmount());
            String dollars = amountInCents.substring(0, amountInCents.length() - 2);
            String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
            this.amount = "$" + dollars + "." + cents;
            if (transaction.getDate() != null) {
                this.date = transaction.getDate().toString();
            } else {
                this.date = "";
            }
            if (transaction.getPayee() != null) {
                this.payeeName = transaction.getPayee().getName();
            } else {
                this.payeeName = "";
            }
            if (transaction.getType() != null) {
                this.typeName = transaction.getType().getName();
            } else {
                this.typeName = "";
            }
            this.tagNames = "";
            if (transaction.getTagList() != null) {
                List<Tag> tags = transaction.getTagList();
                for (int i = 0; i < tags.size(); i++) {
                    this.tagNames += tags.get(i).getName();
                    if (i != tags.size() - 1) {
                        this.tagNames += ", ";
                    }
                }
            }
            if (transaction.isPending()) {
                this.pending = "Pending";
            } else {
                this.pending = "Cleared";
            }

            this.transaction = transaction;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPayeeName() {
            return payeeName;
        }

        public void setPayeeName(String payeeName) {
            this.payeeName = payeeName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTagNames() {
            return tagNames;
        }

        public void setTagNames(String tagNames) {
            this.tagNames = tagNames;
        }

        public String getPending() {
            return pending;
        }

        public void setPending(String pending) {
            this.pending = pending;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
    }
}
