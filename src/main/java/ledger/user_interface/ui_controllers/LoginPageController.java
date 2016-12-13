package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import ledger.controller.DbController;
import ledger.exception.StorageException;
import ledger.user_interface.utils.InputSanitization;

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
        if (InputSanitization.isStringInvalid(this.filePath) || InputSanitization.isStringInvalid(this.pwd))
            return;

        try {
            DbController.INSTANCE.initialize(this.filePath, this.pwd);

//            MainPageController mainPageController = new MainPageController();
//            Scene scene = new Scene(mainPageController);
//            Stage currStage = (Stage) this.getScene().getWindow();
//            currStage.close();
//            Stage newStage = new Stage();
//            newStage.setMinWidth(780);
//            newStage.setWidth(1280);
//            newStage.setHeight(800);
//            newStage.setMinHeight(5000);
//            newStage.setResizable(true);
//            newStage.setScene(scene);
//            newStage.setTitle("Ledger: Transaction View");
//            newStage.show();
            Startup.INSTANCE.newStage(new Scene(new MainPageController()), "Ledger");
        } catch (StorageException e) {
            this.setupErrorPopup("Unable to connect to database", e);
        }
    }

    private void openCreateFilePopup() {
        CreateDatabaseController controller = new CreateDatabaseController();
        Scene scene = new Scene(controller);
        this.createModal(scene, "Create New File");
    }

    public void setChosenFile(File file) {
        this.filePath = file.getAbsolutePath();
        this.chooseFileBtn.setText(file.getName());
        this.password.requestFocus();
    }

}
