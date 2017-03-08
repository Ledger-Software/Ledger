package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.ClearedColumn}
 */
public class ClearedEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Boolean>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Boolean> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        boolean pendingToSet = t.getNewValue();

        transaction.setPending(pendingToSet);

        TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction pending field.", e));

        task.startTask();
        task.waitForComplete();
    }
}
