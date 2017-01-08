package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ledger.updater.GitHubChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/7/2017.
 */
public class UpdateConfirmation extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/UpdateConfirmation.fxml";
    private final GitHubChecker.Release release;

    @FXML
    public Text titleText;
    @FXML
    public Text versionText;
    @FXML
    public Button cancelButton;
    @FXML
    public Button updateButton;

    public UpdateConfirmation(GitHubChecker.Release release) {
        this.release = release;
        this.initController(pageLoc, this, "Error on loading Update Confirmation:");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.titleText.setText("Title: " + release.getName());
        this.versionText.setText("Version: " + release.getVersion());

        cancelButton.setOnAction((ae) -> ((Stage)this.getScene().getWindow()).close());
        updateButton.setOnAction(this::downloadUpdate);
    }

    private void downloadUpdate(ActionEvent actionEvent) {
        try {
            URL website = new URL(release.getDownloadURL());
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());

            File jar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            String jarFolder = jar.getParent();
            String newJar = jarFolder + File.separator + release.getDownloadName();

            FileOutputStream fos = new FileOutputStream(newJar);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();

            new ProcessBuilder(
                    "java", "-jar", newJar).start();

            System.exit(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
