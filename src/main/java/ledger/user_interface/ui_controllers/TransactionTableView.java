package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.*;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.*;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class TransactionTableView extends TableView<TransactionModel> implements IUIController, Initializable {

    private final static String pageLoc = "/fxml_files/TransactionTableView.fxml";

    private ObservableList<Payee> observableAllPayees;
    private Account accountFilter;
    private String searchFilterString = "";

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
    @FXML
    private TableColumn noteColumn;

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<TransactionModel, String>> amountEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
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
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction amount.", e);
            });
//                task.RegisterSuccessEvent(() -> updateTransactionTableView());
            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, LocalDate>> dateEditHandler = new EventHandler<CellEditEvent<TransactionModel, LocalDate>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, LocalDate> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            LocalDate localDateToSet = t.getNewValue();

            java.util.Date dateToSet = java.sql.Date.valueOf(localDateToSet);

            Transaction transaction = model.getTransaction();
            transaction.setDate(dateToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction date.", e);
            });

            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Payee>> payeeEditHandler = new EventHandler<CellEditEvent<TransactionModel, Payee>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Payee> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Payee payeeToSet = t.getNewValue();

            Transaction transaction = model.getTransaction();
            transaction.setPayee(payeeToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction payee.", e);
            });

            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Type>> typeEditHandler = new EventHandler<CellEditEvent<TransactionModel, Type>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Type> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Type typeToSet = t.getNewValue();

            Transaction transaction = model.getTransaction();
            transaction.setType(typeToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction type.", e);
            });

            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, String>> categoryEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String tagsNamesToSet = t.getNewValue();
            String[] tagNames = tagsNamesToSet.split(", ");


            TaskWithReturn<List<Tag>> tagQuery = DbController.INSTANCE.getAllTags();
            tagQuery.startTask();
            List<Tag> allTags = tagQuery.waitForResult();

            ArrayList<Tag> tagsToSet = new ArrayList<>();

            for (String currentTagName : tagNames) {
                Tag currentTagToSet = new Tag(currentTagName, "");
                for (Tag currentTag : allTags) {
                    if (currentTag.getName().equals(currentTagName)) {
                        currentTagToSet = currentTag;
                        break;
                    }
                }
                tagsToSet.add(currentTagToSet);
            }

            Transaction transaction = model.getTransaction();
            transaction.setTagList(tagsToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction categories.", e);
            });

            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    private EventHandler<CellEditEvent<TransactionModel, Boolean>> closedEditHandler = new EventHandler<CellEditEvent<TransactionModel, Boolean>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Boolean> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            boolean pendingToSet = t.getNewValue();

            Transaction transaction = model.getTransaction();
            transaction.setPending(pendingToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                asyncTableUpdate();
                setupErrorPopup("Error editing transaction pending field.", e);
            });

            task.startTask();
            task.waitForComplete();
            updateTransactionTableView();
        }
    };

    public TransactionTableView() {
        this.initController(pageLoc, this, "Error on main page startup: ");
    }

    private void asyncTableUpdate() {
        Startup.INSTANCE.runLater(this::updateTransactionTableView);
    }

    private void configureTransactionTableView() {
        TableRowExpanderColumn<TransactionModel> expanderColumn = new TableRowExpanderColumn<TransactionModel>(param->{

            Note note = param.getValue().getTransaction().getNote();

            NoteEditInputController noteEditInputController = new NoteEditInputController();
            noteEditInputController.setTableRowData(param);
            return noteEditInputController;
        });
        expanderColumn.setText("Note");
        expanderColumn.setCellFactory(param -> new CollapseExpandButton<>(expanderColumn));
        this.getColumns().add(expanderColumn);
        this.amountColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("amount"));
        this.dateColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Date>("date"));
        this.payeeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Payee>("payee"));
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Type>("type"));
        this.categoryColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("tagNames"));
        this.clearedColumn.setCellValueFactory(new PropertyValueFactory<TransactionModel, Boolean>("pending"));

        this.amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.amountColumn.setOnEditCommit(this.amountEditHandler);
        this.amountColumn.setComparator(new AmountComparator());

        this.dateColumn.setCellFactory(column -> {
            return new LocalDateTableCell<>(dateColumn);
        });
        this.dateColumn.setOnEditCommit(this.dateEditHandler);

        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();

        this.observableAllPayees = FXCollections.observableList(allPayees);
        this.payeeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), observableAllPayees));
        this.payeeColumn.setOnEditCommit(this.payeeEditHandler);
        this.payeeColumn.setComparator(new PayeeComparator());

        TaskWithReturn<List<Type>> getAllTypesTask = DbController.INSTANCE.getAllTypes();
        getAllTypesTask.startTask();
        List<Type> allTypes = getAllTypesTask.waitForResult();

        ObservableList<Type> observableAllTypes = FXCollections.observableList(allTypes);
        this.typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new TypeStringConverter(), observableAllTypes));
        this.typeColumn.setOnEditCommit(this.typeEditHandler);
        this.typeColumn.setComparator(new TypeComparator());

        this.categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.categoryColumn.setOnEditCommit(this.categoryEditHandler);
        // TODO: create and set tag comparator

        ObservableList<Boolean> observableAllPending = FXCollections.observableArrayList(true, false);

        this.clearedColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new PendingStringConverter(), observableAllPending));
        this.clearedColumn.setOnEditCommit(this.closedEditHandler);

        // Add ability to delete transactions form tableView
        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                //Put your awesome application specific logic here
                if (t.getCode() == KeyCode.DELETE) {
                    handleDeleteSelectedTransactionsFromTableView();
                }
            }
        });

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu menu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Delete Selected Transaction(s)");
        menu.getItems().add(removeMenuItem);
        this.setContextMenu(menu);
        // removeMenuItem will remove the row from the table:
        removeMenuItem.setOnAction(event -> handleDeleteSelectedTransactionsFromTableView());
    }

    public void updateTransactionTableView() {
        // Update table rows
        TaskWithReturn<List<Transaction>> task;
        if (accountFilter == null) {
            task = DbController.INSTANCE.getAllTransactions();
        } else {
            task = DbController.INSTANCE.getAllTransactionsForAccount(accountFilter);
        }
        task.startTask();
        List<Transaction> transactions = task.waitForResult();

        ArrayList<TransactionModel> models = new ArrayList<>();
        for (Transaction trans : transactions) {
            models.add(new TransactionModel(trans));
        }
        ObservableList<TransactionModel> observableTransactionModels = FXCollections.observableList(models);

//        this.setItems(observableTransactionModels);

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<TransactionModel> filteredData = new FilteredList<>(observableTransactionModels, p -> true);

        // 2. Set the filter Predicate.
        filteredData.setPredicate(transactionModel -> {
            // If filter text is empty, display all persons.
            if (searchFilterString == null || searchFilterString.isEmpty()) {
                return true;
            }

            // Compare first name and last name of every person with filter text.
            String lowerCaseFilter = searchFilterString.toLowerCase();

            if (transactionModel.getAmount().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches amount.
            } else if (transactionModel.getPayee().getName().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches Payee name.
            } else if (transactionModel.getTagNames().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches tags.
            } else {
                return false; // Filter does not match.
            }
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<TransactionModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(this.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        this.setItems(sortedData);

        // Update Payee dropdown
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();

        for (Payee currentPayee : allPayees) {
            if (!this.observableAllPayees.contains(currentPayee)) {
                this.observableAllPayees.add(currentPayee);
            }
        }
    }

    private void handleDeleteSelectedTransactionsFromTableView() {
        List<Integer> indices = new ArrayList<>();
        // Add indices to new list so they aren't observable
        indices.addAll(this.getSelectionModel().getSelectedIndices());
        if (indices.size() != 0) {

            //TODO: Get around this scary mess
            if (indices.contains(new Integer(-1))) {
                indices = this.getSelectionModel().getSelectedIndices();
            }

            for (int i : indices) {
                Transaction transactionToDelete = this.getItems().get(i).getTransaction();

                TaskWithArgs<Transaction> task = DbController.INSTANCE.deleteTransaction(transactionToDelete);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate listView w/ transactions from DB
        configureTransactionTableView();
        updateTransactionTableView();

        DbController.INSTANCE.registerTransationSuccessEvent(this::asyncTableUpdate);
    }

    public void updateAccountFilter(Account accountToFilterBy) {
        this.accountFilter = accountToFilterBy;
        this.asyncTableUpdate();
    }

    public void updateSearchFilterString(String searchFilterString) {
        this.searchFilterString = searchFilterString;
        this.asyncTableUpdate();
    }

    /**
     * Private Button TableCell that includes the logic for the expand and collapse.
     * @param <TransactionModel>
     */
    private class CollapseExpandButton<TransactionModel> extends TableCell<TransactionModel, Boolean> {
        private Button button = new Button();

        CollapseExpandButton(TableRowExpanderColumn<TransactionModel> column) {
            button.setOnAction(event -> column.toggleExpanded(getIndex()));

        }

        protected void updateItem(Boolean expanded, boolean empty) {
            super.updateItem(expanded, empty);
            if (expanded == null || empty) {
                setGraphic(null);
            } else {
                button.setText(expanded ? "Collapse Note " : "Expand Note");
                setGraphic(button);
            }
        }
    }
}
