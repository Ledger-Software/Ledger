package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls the input gathered from the Import Transaction UI (gets the file to import and type)
 */
public class ImportTransactionsPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private Button chooseTrxnFileBtn;
    @FXML
    private ChoiceBox fileExtChooser;

    private File file;
    private static String pageLoc = "/fxml_files/ImportTransactionsPopup.fxml";

    ImportTransactionsPopupController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            this.setupErrorPopup("Error on transaction popup startup: " +  e);
        }
    }

    /**
     * Sets up action listener for the button on the page
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
        this.chooseTrxnFileBtn.setOnAction((event) -> {
            try {
                selectTransactionsFile();
            } catch (Exception e) {
                this.setupErrorPopup("Error on transaction submission: " + e);
            }
        });
    }

    /**
     * Opens a file selector window so the user can choose what file they wish to import.
     *
     * @return void
     */
    private void selectTransactionsFile() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(chooseTrxnFileBtn.getScene().getWindow());
        if(selectedFile != null){
            this.file = selectedFile;
            chooseTrxnFileBtn.setText(selectedFile.getName());
        }
    }

    // TODO: add file extension selection functionality
}
