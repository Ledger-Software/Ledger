package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import ledger.controller.DbController;
import ledger.exception.StorageException;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Button that redirects to login screen and properly closes the database file
 */
public class LogoutButton extends Button implements IUIController, Initializable {
    private static String pageLoc = "/fxml_files/Button.fxml";

    public LogoutButton() {
        this.initController(pageLoc, this, "Unable to load Logout Button");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnAction(this::OnClick);
    }

    private void OnClick(ActionEvent actionEvent) {
        try {
            DbController.INSTANCE.shutdown();
        } catch (StorageException e) {
            this.setupErrorPopup("Database file did not close properly.", e);
        }
        LoginPageController login = new LoginPageController();
        Scene scene = new Scene(login);
        Startup.INSTANCE.newStage(scene, "Leger Login");
    }
}
