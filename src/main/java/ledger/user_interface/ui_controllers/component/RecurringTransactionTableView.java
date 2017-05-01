package ledger.user_interface.ui_controllers.component;

import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RecurringTransactionTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/AutoTaggingTableView.fxml";


    public RecurringTransactionTableView() {
        this.initController(pageLoc, this, "Error creating Recurring Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //DbController.INSTANCE.registerTransactionSuccessEvent((ignored) -> this.asyncUpdateTableView());
        updateTableView();
    }

    private void updateTableView() {
        TaskWithReturn<List<RecurringTransaction>> task = DbController.INSTANCE.getAllRecurringTransactions();
        task.startTask();
        List<RecurringTransaction> transactions = task.waitForResult();
        this.getItems().clear();
        this.getItems().addAll(transactions);
    }

    private void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTableView);
    }
}
