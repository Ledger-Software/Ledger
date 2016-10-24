package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    MainPageController() {
        this.initController(pageLoc, this, "Error on main page startup: ");
    }

    /**
     * Sets up action listeners for the page, allowing for navigation
     *
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
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
    }

    private void createImportTransPopup() {
        try {
            ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
            Scene scene = new Scene(importTrxnController);
            this.createModal(scene, "Import Transactions");
        } catch (Exception e) {
            this.setupErrorPopup("Error on triggering import transactions screen: " + e);
        }
    }

    private void createExpenditureChartsPage() {
        try {
            ExpenditureChartsController chartController = new ExpenditureChartsController();
            Scene scene = new Scene(chartController);
            this.createModal(scene, "Expenditure Charts");
        } catch (Exception e) {
            this.setupErrorPopup("Error on triggering expenditure charts screen: " + e);
        }
    }

    private void createAddTransPopup() {
        try {
            TransactionPopupController trxnController = new TransactionPopupController();
            Scene scene = new Scene(trxnController);
            this.createModal(scene, "Add Transaction");
        } catch (Exception e) {
            this.setupErrorPopup("Error on triggering add transaction screen: " + e);
        }
    }

    private void createAccountPopup() {
        try {
            AccountPopupController accountController = new AccountPopupController();
            Scene scene = new Scene(accountController);
            this.createModal(scene, "Add Account");
        } catch (Exception e) {
            this.setupErrorPopup("Error on triggering add account screen: " + e);
        }
    }
}
