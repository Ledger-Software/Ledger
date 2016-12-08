package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls a popup that has a title and content text area that can be easily modified to
 * fit whatever purpose we need
 */
public class GenericPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private Text errorText;
    @FXML
    private Button okayBtn;
    @FXML
    private Text popupTitle;

    private final static String pageLoc = "/fxml_files/GenericPopup.fxml";
    private String msg;
    private String windowTitle;

    GenericPopupController(String msg, String windowTitle) {
        this.msg = msg;
        this.windowTitle = windowTitle;
        this.initController(pageLoc, this, "Popup startup error: ");
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
        this.errorText.setText(this.msg);
        this.popupTitle.setText(this.windowTitle);
        this.okayBtn.setOnAction((event) -> close());
    }

    /**
     * Closes the popup
     */
    private void close() {
        Stage thisStage = (Stage) this.getScene().getWindow();
        thisStage.close();
    }
}
