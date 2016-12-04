package ledger.user_interface.ui_controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.exception.StorageException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordPromptController extends Pane implements IUIController, Initializable {

    @FXML
    private PasswordField password;
    @FXML
    private Button loginBtn;
    @FXML
    public void onEnter(ActionEvent ae) {
        this.reconnect();
    }

    public final static String pageLoc = "/fxml_files/PasswordPrompt.fxml";

    PasswordPromptController() {
        this.initController(pageLoc, this, "Error displaying password prompt: ");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loginBtn.setOnAction(event -> reconnect());
    }
    private void reconnect() {
        File dbFile = DbController.INSTANCE.getDbFile();
        try {
            DbController.INSTANCE.initialize(dbFile.getAbsolutePath(), this.password.getText());
        } catch (StorageException e) {
            this.setupErrorPopup("Unable to reconnect to database.", e);
            System.err.println(e.getStackTrace());
        }
        ((Stage) this.getScene().getWindow()).close();
    }

}
