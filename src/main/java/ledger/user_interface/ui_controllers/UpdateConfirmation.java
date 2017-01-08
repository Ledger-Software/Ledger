package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ledger.updater.GitHubChecker;

import java.io.*;
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
    @FXML
    public ProgressBar progressBar;

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
        updateButton.setDisable(true);

        new Thread(() -> {
            try {
                URL website = new URL(release.getDownloadURL());
                InputStream in = website.openStream();

                File jar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                String jarFolder = jar.getParent();
                String newJar = jarFolder + File.separator + release.getDownloadName();

                byte[] buffer = new byte[65536];
                FileOutputStream fos = new FileOutputStream(newJar);

                int count;
                long total = 0;
                long finalSize = release.getDownloadAsset().size;
                while((count = in.read(buffer, 0, buffer.length)) != -1) {
                    total += count;
                    double percent = total/(double)finalSize;
                    progressBar.setProgress(percent);
                    fos.write(buffer,0,count);
                }
                in.close();
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
        }).start();
    }
}
