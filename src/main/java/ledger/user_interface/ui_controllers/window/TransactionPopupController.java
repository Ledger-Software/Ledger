package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.UserTransactionInput;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controls and manipulates the input given by the user when manually adding a transaction
 */
public class TransactionPopupController extends GridPane implements Initializable, IUIController {
    private final static String pageLoc = "/fxml_files/AddTransactionPopup.fxml";

    @FXML
    private Button addTrnxnSubmitButton;

    @FXML
    private UserTransactionInput transactionInput;

    public TransactionPopupController() {
        this.initController(pageLoc, this, "Error on transaction popup startup: ");
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

        this.addTrnxnSubmitButton.setOnAction((event) -> {
            Transaction transaction = transactionInput.getTransactionSubmission();
            if (transaction != null) {
                TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(transaction);
                task.RegisterSuccessEvent(() -> closeWindow());
                task.RegisterFailureEvent((e) -> e.printStackTrace());

                task.startTask();
            }
        });
    }

    private void closeWindow() {
        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).close());
    }
}
