package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;

import java.net.URL;
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
    private TransactionTableView transactionTableView;

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

        DbController.INSTANCE.registerTransationSuccessEvent(this::asyncTableUpdate);
    }

    private void asyncTableUpdate() {
        Startup.INSTANCE.runLater(this.transactionTableView::updateTransactionTableView);
    }

//    private void updateTransactionTableView() {
//        try {
//            TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
//            task.startTask();
//            List<Transaction> allTransactions = task.waitForResult();
//
//            ArrayList<TransactionModel> models = new ArrayList<>();
//            for (int i = 0; i < allTransactions.size(); i++) {
//                TransactionModel modelToAdd = new TransactionModel(allTransactions.get(i));
//                models.add(modelToAdd);
//            }
//            ObservableList<TransactionModel> observableTransactionModels = FXCollections.observableList(models);
//
//            this.transactionTableView.setItems(observableTransactionModels);
//
//        } catch (StorageException e) {
//            setupErrorPopup("Error loading all transactions into list view.", e);
//        }
//    }

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
