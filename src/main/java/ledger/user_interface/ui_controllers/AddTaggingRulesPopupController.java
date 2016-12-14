package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls what happens with the data taken from the Add Tagging Rules UI.
 */
public class AddTaggingRulesPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private Button taggingDoneBtn;
    @FXML
    private TagInput tagInput;
    private final static String pageLoc = "/fxml_files/AddTaggingRulesPopupController.fxml";


    AddTaggingRulesPopupController() {
        this.initController(pageLoc, this, "Add tagging rules popup startup error: ");
    }

    /**
     * Sets up action listener for the button on the page
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
        this.taggingDoneBtn.setOnAction((event) -> {

        });
    }
    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
    }

}
