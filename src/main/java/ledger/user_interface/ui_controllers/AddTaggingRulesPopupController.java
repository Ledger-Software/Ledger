package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.user_interface.utils.PayeeStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls what happens with the data taken from the Add Tagging Rules UI.
 */
public class AddTaggingRulesPopupController extends GridPane implements Initializable, IUIController {

    @FXML
    private Button taggingDoneBtn;
    @FXML
    private TagInput tagInput;
    @FXML
    private ComboBox<Payee> payeeText;
    private final static String pageLoc = "/fxml_files/AddTaggingRulesPopup.fxml";


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


        this.payeeText.setDisable(true);


        TaskWithReturn<List<Payee>> payeesTask = DbController.INSTANCE.getAllPayees();
        payeesTask.RegisterFailureEvent((e) -> e.printStackTrace());
        payeesTask.RegisterSuccessEvent((list) -> {
            ObservableList<Payee> payees = FXCollections.observableList(list);
            this.payeeText.setItems(payees);
            this.payeeText.setConverter(new PayeeStringConverter());
            this.payeeText.setEditable(true);
            this.payeeText.setDisable(false);
        });
        payeesTask.startTask();
        this.payeeText.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    this.tagInput.setPayee(newValue);
                }
        );
        this.taggingDoneBtn.setOnAction((event) -> {
            closeWindow();
        });
    }
    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
    }

}
