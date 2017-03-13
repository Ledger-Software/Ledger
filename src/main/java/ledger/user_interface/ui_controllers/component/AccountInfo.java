package ledger.user_interface.ui_controllers.component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ledger.database.entity.Account;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * UI object to hold information about both the account and the display shown in the Account list.
 */
public class AccountInfo extends GridPane implements IUIController, Initializable {

    @FXML
    private Label accountName;
    @FXML
    private AccountBalanceLabel accountBalance;

    private static final String pageLoc = "/fxml_files/AccountInfoItem.fxml";

    private Account currentAccount;

    public AccountInfo(Account a) {
        this.currentAccount = a;
        this.initController(pageLoc, this, "Unable to load Account Information Item");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.accountName.setText(this.currentAccount.getName());
        this.accountBalance.calculateBalance(this.currentAccount);
    }

    /**
     * Returns the current account object stored herein.
     *
     * @return Account a
     */
    public Account getAccount() {
        return this.currentAccount;
    }


    /**
     * Call to set the all accounts balance for the 'All Accounts' aggregate.
     */
    public void setAllAccountBalance() {
        this.accountBalance.calculateBalanceForAllAccounts();
    }
}
