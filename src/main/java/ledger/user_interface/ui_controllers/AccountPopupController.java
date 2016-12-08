package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Account;
import ledger.database.entity.AccountBalance;
import ledger.user_interface.utils.InputSanitization;

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
    private final static String pageLoc = "/fxml_files/AddAccountPopup.fxml";


    AccountPopupController() {
        this.initController(pageLoc, this, "Add account popup startup error: ");
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
            Account account = getAccountSubmission();
            AccountBalance balance = getAccountBalance();
            if (account == null) {
                this.setupErrorPopup("Required Account field is null.", new Exception());
                return;
            }
            if (balance == null) {
                this.setupErrorPopup("Account starting amount must be an integer.", new Exception());
                return;
            }
            TaskWithArgs<Account> task = DbController.INSTANCE.insertAccount(account);
            task.RegisterSuccessEvent(this::insertDone);
            task.RegisterFailureEvent(this::insertFail);
            task.startTask();
        });
    }

    private void insertDone() {
        Startup.INSTANCE.runLater(() -> {
            ((Stage) this.getScene().getWindow()).close();
        });
    }

    private void insertFail(Exception e) {
        this.setupErrorPopup("Account insertion error.", e);
        e.printStackTrace();
    }


    /**
     * Takes the user input and creates a new Account object
     *
     * @return a new Account object
     */
    public Account getAccountSubmission() throws NullPointerException {
        if (InputSanitization.isStringInvalid(accountNameText.getText())) {
            return null;
        } else if (InputSanitization.isStringInvalid(accountDescription.getText())) {
            return null;
        }
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
    public AccountBalance getAccountBalance() throws NullPointerException, NumberFormatException {
        AccountBalance ab;
        int amount = 0;
        if (InputSanitization.isInvalidAmount(accountAmtText.getText())) {
            return null;
        }
        amount = Integer.parseInt(accountAmtText.getText());
        ab = new AccountBalance(this.act, new Date(), amount);
        return ab;
    }
}
