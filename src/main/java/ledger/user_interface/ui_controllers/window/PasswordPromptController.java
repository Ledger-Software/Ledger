package ledger.user_interface.ui_controllers.window;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.exception.StorageException;
import ledger.user_interface.ui_controllers.IUIController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordPromptController extends GridPane implements IUIController, Initializable {

    @FXML
    private PasswordField password;
    @FXML
    private Button loginBtn;

    @FXML
    public void onEnter(ActionEvent ae) {
        this.reconnect();
    }

    private final static String pageLoc = "/fxml_files/PasswordPrompt.fxml";

    public PasswordPromptController() {
        this.initController(pageLoc, this, "Error displaying password prompt: ");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loginBtn.setOnAction(event -> reconnect());
    }

    private void reconnect() {
        Boolean loginSuccess = true;
        File dbFile = DbController.INSTANCE.getDbFile();
        try {
            DbController.INSTANCE.initialize(dbFile.getAbsolutePath(), this.password.getText());
        } catch (StorageException e) {
            loginSuccess = false;
            this.setupErrorPopup("Incorrect Password!\nPlease try again.", new Exception());
            e.getStackTrace();
        }
        if (loginSuccess) ((Stage) this.getScene().getWindow()).close();
    }

}
