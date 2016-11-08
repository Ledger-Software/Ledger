package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Jesse Shellabarger on 11/8/2016.
 */
public class FilteringAccountDropdown extends AccountDropdown {

    private final static Account allAccounts = new Account("All Accounts", "View all transactions");
    private static final String pageLoc = "/fxml_files/ChoiceBox.fxml";

    public FilteringAccountDropdown() {
        this.initController(pageLoc, this, "Unable to load Filtering Account Dropdown");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateAccounts();
        DbController.INSTANCE.registerAccountSuccessEvent(() -> Startup.INSTANCE.runLater(this::updateAccounts));
    }

    private void updateAccounts() {
        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.startTask();
        List<Account> accounts = task.waitForResult();

        accounts.add(allAccounts);

        this.setItems(FXCollections.observableArrayList(accounts));
    }

    @Override
    public Account getSelectedAccount() {
        if (this.getValue() == allAccounts) {
            return null;
        }
        return this.getValue();
    }
}
