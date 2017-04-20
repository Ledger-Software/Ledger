package ledger.user_interface.ui_controllers.window;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by richarel on 4/19/2017.
 */
public class RecurringBillPaymentWindow extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/RecurringBillPaymentWindow.fxml";

    public RecurringBillPaymentWindow() {
        this.initController(pageLoc, this, "Unable to load recurring bill payment window");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}