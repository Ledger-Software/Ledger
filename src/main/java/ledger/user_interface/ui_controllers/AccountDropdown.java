package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.exception.StorageException;

import java.util.List;

/**
 * JavaFx Dropdown that is linked to Account
 */
public class AccountDropdown extends ChoiceBox<Account> {

    public AccountDropdown() {
        try {
            TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
            task.startTask();
            List<Account> accounts = task.waitForResult();

            this.setItems(FXCollections.observableArrayList(accounts));
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    public Account getSelectedAccount() {
        return this.getValue();
    }
}
