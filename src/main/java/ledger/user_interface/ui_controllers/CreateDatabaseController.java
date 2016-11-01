package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ledger.user_interface.utils.InputSanitization;
import ledger.controller.DbController;
import ledger.exception.StorageException;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateDatabaseController extends Pane implements IUIController, Initializable {

    @FXML
    private TextField fileName;
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Button submitButton;

    @FXML
    public void onEnter(ActionEvent ae) {
        submitForm();
    }

    public final static String pageLoc = "/fxml_files/CreateDatabasePage.fxml";

    CreateDatabaseController() {
        this.initController(pageLoc, this, "Error on new database startup: ");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.submitButton.setOnAction(event -> submitForm());
    }

    private void submitForm() {
        String fileName = this.fileName.getText();
        if (InputSanitization.isStringInvalid(fileName)) {
            setupErrorPopup("Please provide a file name", new Exception());
            return;
        }
        if (InputSanitization.isStringInvalid(this.password.getText())) {
            this.setupErrorPopup("Password must exist!", new Exception());
            return;
        }
        if (!this.password.getText().equals(this.confirmPassword.getText())) {
            this.setupErrorPopup("The two passwords provided do not match!", new Exception());
            return;
        }

        try {
            DbController.INSTANCE.initialize("~/" + this.fileName.getText(), this.password.getText());

            Startup.INSTANCE.switchScene(new Scene(new MainPageController()), "Ledger");

            Startup.INSTANCE.runLater(() -> {
                ((Stage) this.getScene().getWindow()).close();
            });

        } catch (StorageException e) {
            this.setupErrorPopup("Unable to connect to database", e);
        }
    }
}
