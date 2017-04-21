package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import ledger.controller.DbController;
import ledger.exception.StorageException;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.InputSanitization;
import ledger.user_interface.utils.PreferenceHandler;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls application startup - handles passwords and lets user select a file for the database
 */
public class LoginPageController extends GridPane implements Initializable, IUIController {

    private final static String pageLoc = "/fxml_files/LoginPage.fxml";
    @FXML
    private Button chooseFileBtn;
    @FXML
    private TextField password;
    @FXML
    private Button loginBtn;
    @FXML
    private Button newFileBtn;
    private String filePath;
    private File directory;


    public LoginPageController() {
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
        this.password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        String lastDBFile = PreferenceHandler.getStringPreference(PreferenceHandler.LAST_DATABASE_FILE_KEY);
        if (lastDBFile != null) {
            File file = new File(lastDBFile);
            if (file.exists() && !file.isDirectory()) {
                this.setChosenFile(file);
            }
            directory = file.getParentFile();
        } else {
            setUpIntroHelp();
        }

    }

    /**
     * Upon first use of the system, opens a help dialog to assist user.
     */
    private void setUpIntroHelp() {
        Alert a = new Alert(Alert.AlertType.NONE);
        String message = "Hello, new user! We've noticed this is your first time using TransACT. " +
                "To get started, please create a new database file and password by clicking the " +
                "'New File' button.";
        this.createIntroductionAlerts("Hello!", message, a);
    }

    /**
     * Opens a file selector window so the user can choose what file they wish to use.
     */
    private void selectFile() {
        FileChooser chooser = new FileChooser();
        if (directory == null)
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        else
            chooser.setInitialDirectory(directory);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Ledger files (*.mv.db)", "*.mv.db");
        chooser.getExtensionFilters().add(extFilter);
        File selectedFile = chooser.showOpenDialog(chooseFileBtn.getScene().getWindow());
        if (selectedFile != null) {
            setChosenFile(selectedFile);
        }
    }

    private void login() {
        String pwd = this.password.getText();
        if (InputSanitization.isStringInvalid(this.filePath)) {
            this.setupErrorPopup("Please select a file or create a new one.");
            return;
        } else if (InputSanitization.isStringInvalid(pwd)) {
            this.setupErrorPopup("Please enter a password.");
            return;
        }
        try {
            DbController.INSTANCE.initialize(this.filePath, pwd);
            PreferenceHandler.setStringPreference(PreferenceHandler.LAST_DATABASE_FILE_KEY, this.filePath);
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

    private void setChosenFile(File file) {
        this.filePath = file.getAbsolutePath();
        this.chooseFileBtn.setText(file.getName());
        this.password.requestFocus();
    }

}
