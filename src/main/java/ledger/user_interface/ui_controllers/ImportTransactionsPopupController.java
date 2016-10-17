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
 * Created by richarel on 10/17/2016.
 */
public class ImportTransactionsPopupController extends GridPane implements Initializable {

    @FXML
    private Button chooseTrxnFileBtn;
    @FXML
    private ChoiceBox fileExtChooser;

    private File file;

    ImportTransactionsPopupController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/ImportTransactionsPopup.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (Exception e) {
            System.out.println("Error on transaction popup startup: " +  e);
        }
    }

    /**
     * Sets up action listener for the button on the page
     *
     * @param fxmlFileLocation
     * @param resources
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.chooseTrxnFileBtn.setOnAction((event) -> {
            try {
                selectTransactionsFile();
            } catch (Exception e) {
                System.out.println("Error on transaction submission: " + e);
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
