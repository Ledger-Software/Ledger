package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ledger.database.enity.Account;
import ledger.database.enity.AccountBalance;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controls what happens with the data taken from the Add Account UI.
 */
public class AccountPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private Button submitAccountInfo;
    @FXML
    private TextField accountAmtText;
    @FXML
    private TextField accountDescription;
    @FXML
    private TextField accountNameText;

    private Account act = null;
    private static String pageLoc = "/fxml_files/AddAccountPopup.fxml";


    AccountPopupController() {
        this.initController(pageLoc, this, "Error on account popup startup: ");
    }

    /**
     * Sets up action listener for the button on the page
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
        this.submitAccountInfo.setOnAction((event) -> {
            try {
                getAccountSubmission();
                getAccountBalance();
            } catch (Exception e) {
                this.setupErrorPopup("Error on account submission: " + e);
            }
            this.getScene().getWindow().hide();
        });
    }

    /**
     * Takes the user input and creates a new Account object
     *
     * @return a new Account object
     */
    public Account getAccountSubmission() {
        if (act == null) {
            this.act = new Account(accountNameText.getText(), accountDescription.getText());
        }
        return this.act;
    }

    /**
     * Takes the user input and creates a new AccountBalance object
     *
     * @return a new AccountBalance object
     */
    public AccountBalance getAccountBalance() {
        return new AccountBalance(this.act, new Date(), Integer.parseInt(accountAmtText.getText()));
    }
}
