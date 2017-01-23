package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * JavaFx Dropdown that is linked to Account
 */
public class AccountDropdown extends ChoiceBox<Account> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/ChoiceBox.fxml";

    public AccountDropdown() {
        this.initController(pageLoc, this, "Unable to load Account Dropdown");
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

        this.setItems(FXCollections.observableArrayList(accounts));
        if(this.getItems().size()==1){
            this.getSelectionModel().select(0);
        }
    }

    public Account getSelectedAccount() {
        return this.getValue();
    }
}
