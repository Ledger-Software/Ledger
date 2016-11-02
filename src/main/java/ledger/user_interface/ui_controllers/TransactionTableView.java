package ledger.user_interface.ui_controllers;

import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.exception.StorageException;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.InputSanitization;
import ledger.user_interface.utils.PayeeStringConverter;
import ledger.user_interface.utils.PendingStringConverter;
import ledger.user_interface.utils.TypeStringConverter;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class TransactionTableView extends TableView<TransactionModel> implements IUIController {

    private final static String pageLoc = "/fxml_files/TransactionTableView.fxml";
    // Transaction table UI objects
    @FXML
    private TableColumn payeeColumn;
    @FXML
    private TableColumn amountColumn;
    @FXML
    private TableColumn dateColumn;
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
                if (InputSanitization.isInvalidAmount(amountToSetString)) {
                    updateTransactionTableView();
                    setupErrorPopup("Provided amount is invalid", new Exception());
                    return;
                }

                if (amountToSetString.charAt(0) == '$') {
                    amountToSetString = amountToSetString.substring(1);
                }

                double amountToSetDecimal = Double.parseDouble(amountToSetString);
                int amountToSet = (int) Math.round(amountToSetDecimal * 100);

                Transaction transaction = model.getTransaction();
                transaction.setAmount(amountToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction amount.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
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
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction date.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction date.", e);
            } catch (ParseException e) {
                setupErrorPopup("Error parsing date string.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Payee>> payeeEditHandler = new EventHandler<CellEditEvent<TransactionModel, Payee>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Payee> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                Payee payeeToSet = t.getNewValue();

                Transaction transaction = model.getTransaction();
                transaction.setPayee(payeeToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction payee.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction payee.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Type>> typeEditHandler = new EventHandler<CellEditEvent<TransactionModel, Type>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Type> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                Type typeToSet = t.getNewValue();

                Transaction transaction = model.getTransaction();
                transaction.setType(typeToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction type.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction type.", e);
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
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction categories.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction categories.", e);
            }
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Boolean>> closedEditHandler = new EventHandler<CellEditEvent<TransactionModel, Boolean>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Boolean> t) {
            try {
                TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
                boolean pendingToSet = t.getNewValue();

                Transaction transaction = model.getTransaction();
                transaction.setPending(pendingToSet);

                TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error editing transaction pending field.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } catch (StorageException e) {
                setupErrorPopup("Error editing transaction pending field.", e);
            }
        }
    };

    public TransactionTableView() {
//        this.initController(pageLoc, this, "Error on main page startup: ");
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource(pageLoc));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // Populate listView w/ transactions from DB
        configureTransactionTableView();
        updateTransactionTableView();

        DbController.INSTANCE.registerTransationSuccessEvent(this::asyncTableUpdate);
    }

    private void asyncTableUpdate() {
        Startup.INSTANCE.runLater(this::updateTransactionTableView);
    }

    private void configureTransactionTableView() {
        try {
            this.amountColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("amount"));
            this.dateColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("date"));
            this.payeeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Payee>("payee"));
            this.typeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Type>("type"));
            this.categoryColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("tagNames"));
            this.clearedColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Boolean>("pending"));

            this.amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            this.amountColumn.setOnEditCommit(this.amountEditHandler);

            this.dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            this.dateColumn.setOnEditCommit(this.dateEditHandler);

            TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
            getAllPayeesTask.startTask();
            List<Payee> allPayees = getAllPayeesTask.waitForResult();

            ObservableList<Payee> observableAllPayees = FXCollections.observableList(allPayees);
            this.payeeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), observableAllPayees));
            this.payeeColumn.setOnEditCommit(this.payeeEditHandler);

            TaskWithReturn<List<Type>> getAllTypesTask = DbController.INSTANCE.getAllTypes();
            getAllTypesTask.startTask();
            List<Type> allTypes = getAllTypesTask.waitForResult();

            ObservableList<Type> observableAllTypes = FXCollections.observableList(allTypes);
            this.typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new TypeStringConverter(), observableAllTypes));
            this.typeColumn.setOnEditCommit(this.typeEditHandler);


            this.categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            this.categoryColumn.setOnEditCommit(this.categoryEditHandler);

            ObservableList<Boolean> observableAllPending = FXCollections.observableArrayList(true, false);

            this.clearedColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new PendingStringConverter(), observableAllPending));
            this.clearedColumn.setOnEditCommit(this.closedEditHandler);

            // Add ability to delete transactions form tableView
            this.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent t) {
                    //Put your awesome application specific logic here
                    if (t.getCode() == KeyCode.DELETE) {
                        handleDeleteTransactionFromTableView();
                        updateTransactionTableView();
                    }
                }
            });
        } catch (StorageException e) {
            setupErrorPopup("Error populating table view.", e);
        }
    }

    public void updateTransactionTableView() {
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

            this.setItems(observableTransactionModels);

        } catch (StorageException e) {
            setupErrorPopup("Error loading all transactions into list view.", e);
        }
    }

    private void handleDeleteTransactionFromTableView() {
        try {
            int selectedIndex = this.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                TransactionModel model = (TransactionModel) this.getItems().get(selectedIndex);
                Transaction transactionToDelete = model.getTransaction();

                TaskWithArgs<Transaction> task = DbController.INSTANCE.deleteTransaction(transactionToDelete);
                task.RegisterFailureEvent((e) -> {
                    updateTransactionTableView();
                    setupErrorPopup("Error deleting transaction.", e);
                });
                task.RegisterSuccessEvent(() -> updateTransactionTableView());
                task.startTask();
            } else {
                setupErrorPopup("No transactions deleted.", new NullPointerException("No transaction deleted."));
            }
        } catch (StorageException e) {
            setupErrorPopup("Error deleting transaction.", e);
        }
    }
}
