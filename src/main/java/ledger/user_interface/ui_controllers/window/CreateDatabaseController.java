package ledger.user_interface.ui_controllers.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.exception.StorageException;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.InputSanitization;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateDatabaseController extends GridPane implements IUIController, Initializable {
    @FXML
    private TextField password;
    @FXML
    private TextField confirmPassword;
    @FXML
    private Button submitButton;
    @FXML
    private Button saveLocationButton;

    private File saveLocation;

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
        this.saveLocationButton.setOnAction(this::saveLocation);
    }

    private void saveLocation(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("LedgerDB.mv.db");
        fileChooser.setTitle("Select Save File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Ledger files (*.mv.db)", "*.mv.db");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setSelectedExtensionFilter(extFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            this.saveLocationButton.setText(file.getName());
            this.saveLocation = file;
        }
    }

    private void submitForm() {
        if (this.saveLocation == null) {
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
            if (this.saveLocation.exists())
                this.saveLocation.delete();

            DbController.INSTANCE.initialize(saveLocation.getAbsolutePath(), this.password.getText());

            Startup.INSTANCE.switchScene(new Scene(new MainPageController()), "Ledger");

            Startup.INSTANCE.runLater(() -> {
                ((Stage) this.getScene().getWindow()).close();
            });

        } catch (StorageException e) {
            this.setupErrorPopup("Unable to connect to database", e);
        }
    }
}
