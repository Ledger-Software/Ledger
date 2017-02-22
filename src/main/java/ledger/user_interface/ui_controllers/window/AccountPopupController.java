package ledger.user_interface.ui_controllers.window;

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
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.InputSanitization;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controls what happens with the data taken from the Add Account UI.
 */
public class AccountPopupController extends GridPane implements Initializable, IUIController {

    private final static String pageLoc = "/fxml_files/AddAccountPopup.fxml";
    @FXML
    private Button submitAccountInfo;
    @FXML
    private TextField accountAmtText;
    @FXML
    private TextField accountDescription;
    @FXML
    private TextField accountNameText;
    private Account act = null;


    public AccountPopupController() {
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
        this.submitAccountInfo.setOnAction((event) -> this.handleSubmit());
        this.accountAmtText.setOnAction((event) -> this.handleSubmit());
        this.accountDescription.setOnAction((event) -> this.handleSubmit());
        this.accountNameText.setOnAction((event) -> this.handleSubmit());
    }

    private void handleSubmit() {
        Account account = getAccountSubmission();
        AccountBalance balance = getAccountBalance();
        if (balance == null) {
            return;
        } else if (account == null) {
            this.setupErrorPopup("Required Account field(s) are invalid.", new Exception());
            return;
        }
        TaskWithArgs<Account> task = DbController.INSTANCE.insertAccount(account);
        task.RegisterFailureEvent(this::insertFail);
        task.startTask();
        task.waitForComplete();

        TaskWithArgs<AccountBalance> balanceTask = DbController.INSTANCE.addBalanceForAccount(balance);
        balanceTask.RegisterSuccessEvent(this::insertDone);
        balanceTask.RegisterFailureEvent(this::insertFail);
        balanceTask.startTask();
        balanceTask.waitForComplete();
    }

    private void insertDone() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
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
    private Account getAccountSubmission() throws NullPointerException {
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
    private AccountBalance getAccountBalance() throws NullPointerException, NumberFormatException {
        AccountBalance ab;
        long amount;
        if (InputSanitization.isInvalidAmount(accountAmtText.getText())) {
            this.setupErrorPopup("Account starting amount must have a numerical value.", new Exception());
            return null;
        }
        Double tmpAmt = Double.parseDouble(accountAmtText.getText()) * 100;
        amount = tmpAmt.longValue();
        if (amount == Long.MAX_VALUE) {
            this.setupErrorPopup("An account cannot have a balance over " + Long.MAX_VALUE / 100);
            return null;
        } else if (amount == Long.MIN_VALUE) {
            this.setupErrorPopup("An account cannot have a balance under " + Long.MIN_VALUE / 100);
            return null;
        }
        ab = new AccountBalance(this.act, new Date(), amount);
        return ab;
    }
}
