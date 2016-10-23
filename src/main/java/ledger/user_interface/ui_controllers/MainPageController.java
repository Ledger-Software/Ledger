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

    private static String pageLoc = "/fxml_files/MainPage.fxml";

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
            try {
                AccountPopupController accountController = new AccountPopupController();
                Scene scene = new Scene(accountController);
                this.createModal(scene);
            } catch (Exception e) {
                this.setupErrorPopup("Error on triggering add account screen: " + e);
            }
        });

        this.addTransactionBtn.setOnAction((event) -> {
            try {
                TransactionPopupController trxnController = new TransactionPopupController();
                Scene scene = new Scene(trxnController);
                this.createModal(scene);
            } catch (Exception e) {
                this.setupErrorPopup("Error on triggering add transaction screen: " + e);
            }
        });

        this.trackSpendingBtn.setOnAction((event) -> {
            try {
                ExpenditureChartsController chartController = new ExpenditureChartsController();
                Scene scene = new Scene(chartController);
                this.createModal(scene);
            } catch (Exception e) {
                this.setupErrorPopup("Error on triggering expenditure charts screen: " + e);
            }
        });

        this.importTransactionsBtn.setOnAction((event) -> {
            try {
                ImportTransactionsPopupController importTrxnController = new ImportTransactionsPopupController();
                Scene scene = new Scene(importTrxnController);
                this.createModal(scene);
            } catch (Exception e) {
                this.setupErrorPopup("Error on triggering import transactions screen: " + e);
            }
        });
    }
}
