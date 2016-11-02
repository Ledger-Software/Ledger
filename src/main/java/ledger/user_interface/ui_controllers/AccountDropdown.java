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
public class AccountDropdown extends ChoiceBox<Account> implements IUIController {

    public AccountDropdown() {
        updateAccounts();

        DbController.INSTANCE.registerAccountSuccessEvent(this::updateAccounts);
    }

    private void updateAccounts() {
        try {
            TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
            task.startTask();
            List<Account> accounts = task.waitForResult();

            this.setItems(FXCollections.observableArrayList(accounts));
        } catch (StorageException e) {
            this.setupErrorPopup("Error with Account Dropdown.", e);
            e.printStackTrace();
        }
    }

    public Account getSelectedAccount() {
        return this.getValue();
    }
}
