package ledger.user_interface.ui_controllers.window;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by CJ on 5/1/2017.
 */
public class RecurringTransactionEditorWindow extends GridPane implements IUIController, Initializable {

    private static final String pageLoc = "/fxml_files/RecurringTransactionEditorWindow.fxml";

    public RecurringTransactionEditorWindow() {
        this.initController(pageLoc, this, "Unable to load Recurring Transaction Editor");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
