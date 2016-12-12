package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
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
        LoginPageController login = new LoginPageController();
        Scene scene = new Scene(login);
        Stage currStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        currStage.close();
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Ledger Login");
        newStage.show();
        newStage.setMinWidth(newStage.getWidth());
        newStage.setMinHeight(newStage.getHeight());
        try {
            DbController.INSTANCE.shutdown();
        } catch (StorageException e) {
            this.setupErrorPopup("Database file did not close properly.", e);
        }
    }
}
