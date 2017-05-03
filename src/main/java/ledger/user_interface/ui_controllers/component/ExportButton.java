package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import ledger.controller.DbController;
import ledger.exception.StorageException;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.window.LoginPageController;
import ledger.user_interface.ui_controllers.window.PasswordPromptController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Exports the database file to the chosen directory.
 */
public class ExportButton extends Button implements IUIController, Initializable {

    public ExportButton() {
        String pageLoc = "/fxml_files/Button.fxml";
        this.initController(pageLoc, this, "Unable to load Export Button");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnAction(this::OnClick);
    }

    private void OnClick(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Save File");
        File userHome = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(userHome);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Ledger files (*.mv.db)", "*.mv.db");
        chooser.getExtensionFilters().add(extFilter);

        File currentDbFile = DbController.INSTANCE.getDbFile();
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
        String fileName = timeStamp + currentDbFile.getName();
        File newDbFile = new File(userHome.toPath().toString(), fileName);

        int numFiles = 1;
        while (newDbFile.exists()) {
            fileName = timeStamp + "(" + numFiles + ")" + currentDbFile.getName();
            newDbFile = new File(userHome.toPath().toString(), fileName);
            numFiles++;
        }

        chooser.setInitialFileName(newDbFile.getName());
        File saveLocation = chooser.showSaveDialog(this.getScene().getWindow());
        if (saveLocation == null) return;


        try {
            DbController.INSTANCE.shutdown();
            Files.copy(currentDbFile.toPath(), saveLocation.toPath());
        } catch (IOException e) {
            this.setupErrorPopup("Unable to properly copy data.", e);
            e.printStackTrace();
        } catch (StorageException e) {
            this.setupErrorPopup("Unable to properly close database.", e);
            e.printStackTrace();
        }
        this.displayPasswordPrompt();
    }

    private void displayPasswordPrompt() {
        PasswordPromptController promptController = new PasswordPromptController();
        Scene scene = new Scene(promptController);
        this.createModal(scene, "Verify Password", () -> {
            LoginPageController loginController = new LoginPageController();
            Scene Loginscene = new Scene(loginController);
            Startup.INSTANCE.newStage(Loginscene, "Ledger Login", false);
        });

    }
}
