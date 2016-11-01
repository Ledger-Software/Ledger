package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.exception.StorageException;

import java.io.File;
import java.io.IOException;
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
    @FXML
    private Button newFileBtn;
    @FXML
    public void onEnter(ActionEvent ae) {
        login();
    }

    private String pwd;
    private String filePath;
    private final static String pageLoc = "/fxml_files/LoginPage.fxml";

    LoginPageController() {
        this.initController(pageLoc, this, "Error on login startup: ");
    }

    /**
     * Sets up action listeners for the buttons on the page.
     * <p>
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation The location used to resolve relative paths for the root object, or
     *                         <tt>null</tt> if the location is not known.
     * @param resources        The resources used to localize the root object, or <tt>null</tt> if
     *                         the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.loginBtn.setOnAction((event) -> {
            MainPageController mainPageController = new MainPageController();
            Scene scene = new Scene(mainPageController);
            Stage newStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            newStage.setScene(scene);
            newStage.setTitle("Ledger: Transaction View");
            newStage.show();
        });
        this.chooseFileBtn.setOnAction((event -> selectFile()));
        this.newFileBtn.setOnAction((event -> openCreateFilePopup()));
        this.loginBtn.setOnAction((event -> login()));
    }

    /**
     * Opens a file selector window so the user can choose what file they wish to use.
     *
     * @return void
     */
    private void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedFile = chooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if (selectedFile != null) {
            setChosenFile(selectedFile);
        }
    }

    private void login() {
        this.pwd = this.password.getText();
        if (this.filePath.equals("") || this.pwd.equals(""))
            return;

        try {
            DbController.INSTANCE.initialize(this.filePath, this.pwd);

            Startup.INSTANCE.switchScene(new Scene(new MainPageController()), "Ledger");
        } catch (StorageException e) {
            this.setupErrorPopup("Unable to connect to database", e);
        }

    }

    private void openCreateFilePopup() {
        CreateDatabaseController controller = new CreateDatabaseController(this);
        Scene scene = new Scene(controller);
        this.createModal(scene, "Create New File");
    }

    public void setChosenFile(File file) {
        this.filePath = file.getAbsolutePath();
        this.chooseFileBtn.setText(file.getName());
        this.password.requestFocus();
    }
}
