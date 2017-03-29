package ledger.user_interface.ui_controllers.window;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.AutoTaggingTableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Window wrapper for {@link AutoTaggingTableView}
 */
public class AutoTaggingTableWindow extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/AutoTaggingTableWindow.fxml";

    public AutoTaggingTableWindow() {
        this.initController(pageLoc, this, "Unable to load auto tagging editor window");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
