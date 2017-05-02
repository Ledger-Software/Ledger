package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by CJ on 5/1/2017.
 */
public class RecurringTransactionEditorWindow extends GridPane implements IUIController, Initializable {

    private static final String pageLoc = "/fxml_files/RecurringTransactionEditorWindow.fxml";

    @FXML
    private Button doneButton;

    public RecurringTransactionEditorWindow() {
        this.initController(pageLoc, this, "Unable to load Recurring Transaction Editor");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doneButton.setOnAction((ae) -> {
            Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
        });
    }
}
