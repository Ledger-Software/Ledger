package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.CheckNumberColumn}
 */
public class CheckNumberEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Integer>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Integer> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Integer checkNumberToSet = t.getNewValue();

        if (checkNumberToSet == null) {
            setupErrorPopup("Provided check number is invalid", new Exception());
            ((TransactionTableView) t.getTableView()).updateTransactionTableView();
            return;
        }

        transaction.setCheckNumber(checkNumberToSet);

        TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction amount.", e));
        task.startTask();
        task.waitForComplete();
    }
}
