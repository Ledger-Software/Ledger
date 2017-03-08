package ledger.user_interface.ui_controllers.window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.UserTransactionInput;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Takes a list of {@link Transaction} and will ask the user if they want to
 * either import or skip each transaction. Also gives them the ability to edit
 * before importing.
 */
public class GenericImportTransactionPopup extends GridPane implements Initializable, IUIController {

    @FXML
    private Button importButton;
    @FXML
    private Button skipButton;
    @FXML
    private UserTransactionInput transactionInput;
    @FXML
    private Text title;

    private static final String pageLoc = "/fxml_files/GenericImportTransactionPopup.fxml";

    private final List<Transaction> trans;
    private Iterator<Transaction> iterator;
//    private Transaction currentTrans;


    public GenericImportTransactionPopup(String title) {
        this.initController(pageLoc, this, "Unable to load Duplicate Transaction Popup");
        this.trans = new ArrayList<>();
        this.title.setText(title);
    }

    public GenericImportTransactionPopup(List<Transaction> trans, String title) {
        this.trans = trans;
        this.initController(pageLoc, this, "Unable to load Duplicate Transaction Popup");
        this.title.setText(title);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.iterator = this.trans.iterator();
        this.skipButton.setOnAction((ae) -> this.next());
        this.importButton.setOnAction((ae) -> this.importTrans());
        next();
    }

    private void importTrans() {
        TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(transactionInput.getTransactionSubmission());

        task.RegisterSuccessEvent(this::next);
        task.RegisterFailureEvent((e) -> this.next());

        importButton.setDisable(true);
        task.startTask();

    }

    private void next() {
        if (iterator.hasNext()) {
            Transaction currentTrans = iterator.next();
            Startup.INSTANCE.runLater(() -> {
                transactionInput.setTransaction(currentTrans);
                importButton.setDisable(false);
            });
        } else {
            Startup.INSTANCE.runLater(this::closeWindow);
        }
    }


    private void closeWindow() {

        Startup.INSTANCE.runLater(() -> this.getScene().getWindow().fireEvent(new WindowEvent(this.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)));

    }
}
