package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls application startup - handles passwords and lets user select a file for the database
 */
public class LoginPageController extends GridPane implements Initializable, IUIController {

    @FXML
    private Button chooseFileBtn;
    @FXML
    private TextField password;
    @FXML
    private Button loginBtn;

    private String pwd;
    private File file;
    private static String pageLoc = "/fxml_files/LoginPage.fxml";

    LoginPageController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            this.setupErrorPopup("Error on login startup: " +  e);
        }
    }

    /**
     * Sets up action listeners for the buttons on the page.
     *
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.loginBtn.setOnAction((event) -> {
            try {
                MainPageController mainPageController = new MainPageController();
                Scene scene = new Scene(mainPageController);
                Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                newStage.setScene(scene);
                newStage.setTitle("Ledger");
                newStage.show();
            } catch (Exception e) {
                this.setupErrorPopup("Error on login submission: " + e);
            }
        });
    this.chooseFileBtn.setOnAction((event -> selectFile()));
    }

    /**
     * Opens a file selector window so the user can choose what file they wish to use.
     *
     * @return void
     */
    private void selectFile() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if(selectedFile != null){
            this.file = selectedFile;
            chooseFileBtn.setText(selectedFile.getName());
        }
    }
}
