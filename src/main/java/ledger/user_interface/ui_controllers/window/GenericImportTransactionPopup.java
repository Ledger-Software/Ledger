package ledger.user_interface.ui_controllers.window;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
 * Created by CJ on 11/2/2016.
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

    private List<Transaction> trans;
    private Iterator<Transaction> iter;
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
        this.iter = this.trans.iterator();
        this.skipButton.setOnAction(this::next);
        this.importButton.setOnAction(this::importTrans);
        next(null);
    }

    private void importTrans(ActionEvent actionEvent) {
        TaskWithArgs<Transaction> task = DbController.INSTANCE.insertTransaction(transactionInput.getTransactionSubmission());

        task.RegisterSuccessEvent(() -> this.next(null));
        task.RegisterFailureEvent((e) -> this.next(null));

        importButton.setDisable(true);
        task.startTask();

    }

    private void next(ActionEvent actionEvent) {
        if (iter.hasNext()) {
            Transaction currentTrans = iter.next();
            Startup.INSTANCE.runLater(() -> {
                transactionInput.setTransaction(currentTrans);
                importButton.setDisable(false);
            });
        } else {
            Startup.INSTANCE.runLater(this::closeWindow);
        }
    }


    private void closeWindow() {

        Startup.INSTANCE.runLater(() -> ((Stage) this.getScene().getWindow()).fireEvent(new WindowEvent(((Stage) this.getScene().getWindow()), WindowEvent.WINDOW_CLOSE_REQUEST)));

    }
}
