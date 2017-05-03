package ledger.user_interface.ui_controllers.component;

import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.RecurringTransaction;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RecurringTransactionTableView extends AbstractTableView<RecurringTransaction> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/RecurringEditorTableView.fxml";


    public RecurringTransactionTableView() {
        this.initController(pageLoc, this, "Error creating Recurring Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.DELETE) {
                List<Integer> indices = new ArrayList<>();
                // Add indices to new list so they aren't observable
                indices.addAll(this.getSelectionModel().getSelectedIndices());
                if (indices.size() != 0) {

                    //TODO: Get around this scary mess
                    if (indices.contains(-1)) {
                        indices = this.getSelectionModel().getSelectedIndices();
                    }

                    for (int i : indices) {
                        RecurringTransaction transactionToDelete = this.getItems().get(i);

                        TaskNoReturn task = DbController.INSTANCE.deleteReucrringTransaction(transactionToDelete);
                        task.RegisterFailureEvent((e) -> {
                            asyncTableUpdate();
                            setupErrorPopup("Error deleting transaction.", e);
                        });
                        task.startTask();
                        task.waitForComplete();
                    }
                    updateTransactionTableView();
                }
            }
        });

        //DbController.INSTANCE.registerTransactionSuccessEvent((ignored) -> this.asyncUpdateTableView());
        updateTransactionTableView();
    }

    private void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTransactionTableView);
    }

    @Override
    public void updateTransactionTableView() {
        TaskWithReturn<List<RecurringTransaction>> task = DbController.INSTANCE.getAllRecurringTransactions();
        task.startTask();
        List<RecurringTransaction> transactions = task.waitForResult();
        this.getItems().clear();
        this.getItems().addAll(transactions);
    }
}
