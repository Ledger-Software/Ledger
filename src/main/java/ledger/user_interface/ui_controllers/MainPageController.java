package ledger.user_interface.ui_controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Account;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls all input and interaction with the Main Page of the application
 */

public class MainPageController extends GridPane implements Initializable, IUIController {
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
    private FilteringAccountDropdown chooseAccount;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField searchTextField;

    // Transaction table UI objects
    @FXML
    private TransactionTableView transactionTableView;

    private final static String pageLoc = "/fxml_files/MainPage.fxml";

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

        this.deleteAccountBtn.setOnAction( (event) -> {
            deleteAccount();
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

        this.searchButton.setOnAction(this::searchClick);
        this.clearButton.setOnAction(this::clearSearch);

        this.searchTextField.setOnAction(this::searchClick);

        this.chooseAccount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
            @Override
            public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                transactionTableView.updateAccountFilter(chooseAccount.getSelectedAccount());
            }
        });
    }

    private void clearSearch(ActionEvent actionEvent) {
        searchTextField.setText("");
        transactionTableView.updateSearchFilterString("");
    }

    private void searchClick(ActionEvent actionEvent) {
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
        ExpenditureChartsController chartController = new ExpenditureChartsController();
        Scene scene = new Scene(chartController);
        this.createModal(this.getScene().getWindow(), scene, "Expenditure Charts");
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
        if (chooseAccount.getSelectedAccount() == null) {
            setupErrorPopup("Cannot delete the All Accounts aggregation", new Exception());
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Account Delete");
        alert.setHeaderText(null);
        alert.setContentText("Deleting " + chooseAccount.getSelectedAccount().getName() + " will delete all Transactions" +
                " associated with the account. Do you wish to proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            TaskWithArgs<Account> t = DbController.INSTANCE.deleteAccount(chooseAccount.getSelectedAccount());
            t.RegisterSuccessEvent(() -> Startup.INSTANCE.runLater(() -> chooseAccount.selectDefault()));
            t.RegisterFailureEvent((e) -> {
                e.printStackTrace();
            });
            t.startTask();
        } else {
           return;
        }
    }
}
