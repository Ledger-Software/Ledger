package ledger.user_interface.ui_controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls the popup that occurs upon an error so the user can see what's going on
 */
public class ErrorPopupController extends GridPane implements Initializable, IUIController {
    @FXML
    private Label errorText;
    @FXML
    private Button okayBtn;

    private static String pageLoc = "/fxml_files/ErrorPopup.fxml";
    private String errMsg;

    ErrorPopupController(String errMsg) {
        this.errMsg = errMsg;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageLoc));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            this.errorText.setText(this.errMsg);
        } catch (Exception e) {
            System.out.println("Error on error popup startup: " + e);
        }
    }

    /**
     * Sets up action listener for the button on the page
     *
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFilelocation
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.okayBtn.setOnAction((event) -> {
            try {
                close();
            } catch (Exception e) {
                System.out.println("Error on account submission: " + e);
            }
        });
    }

    /**
     * Closes the popup
     */
    private void close() {
        this.getScene().getWindow().hide();
    }
}
