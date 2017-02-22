package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.PayeeStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Dropdown that auto-populates with the Payee's in the database
 */
public class PayeeDropdown extends ComboBox<Payee> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/ComboBox.fxml";

    public PayeeDropdown() {
        this.initController(pageLoc, this, "Unable to load Payee Dropdown");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updatePayee();
        this.setConverter(new PayeeStringConverter());
        this.setEditable(true);
    }

    private void updatePayee() {
        TaskWithReturn<List<Payee>> task = DbController.INSTANCE.getAllPayees();
        task.startTask();
        List<Payee> payees = task.waitForResult();

        this.setItems(FXCollections.observableArrayList(payees));
    }

    public Payee getSelectedPayee() {
        return this.getValue();
    }
}
