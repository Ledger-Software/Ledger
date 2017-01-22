package ledger.user_interface.ui_controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/22/2017.
 */
public class PayeeTableWindow extends GridPane implements IUIController, Initializable {
    private static final String pageLog = "/fxml_files/PayeeTableWindow.fxml";

    public PayeeTableWindow() {
        this.initController(pageLog, this, "Unable to load Payee editor window");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
