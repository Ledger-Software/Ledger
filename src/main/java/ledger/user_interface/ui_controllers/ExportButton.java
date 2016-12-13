package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import ledger.controller.DbController;
import ledger.exception.StorageException;

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

    private static String pageLoc = "/fxml_files/Button.fxml";

    public ExportButton() {
        this.initController(pageLoc, this, "Unable to load Export Button");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnAction(this::OnClick);
    }

    private void OnClick(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File saveLocation = chooser.showDialog(this.getScene().getWindow());
        if (saveLocation == null) return;
        File currentDbFile = DbController.INSTANCE.getDbFile();
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
        String fileName = timeStamp + currentDbFile.getName();
        File newDbFile = new File(saveLocation.toPath().toString(), fileName);

        int numFiles = 1;
        while (newDbFile.exists()) {
            fileName = timeStamp + "(" + numFiles + ")" + currentDbFile.getName();
            newDbFile = new File(saveLocation.toPath().toString(), fileName);
        }

        try {
            DbController.INSTANCE.shutdown();
            Files.copy(currentDbFile.toPath(), newDbFile.toPath());
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
        this.createModal(scene, "Verify Password");
    }
}
