package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

/**
 * Event Handler for Amount Column's Cell Edit Events.
 */
public class AmountEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Long>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Long> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Long amountToSet = t.getNewValue();
        if (amountToSet == null) {
            setupErrorPopup("Provided amount is invalid", new Exception());
            ((TransactionTableView) t.getTableView()).updateTransactionTableView();
            return;
        }

        if ((amountToSet < 0) && (transaction.getType().getName().equals("Account Credit") || transaction.getType().getName().equals("Misc Credit"))) {
            setupErrorPopup("Transactions of the " + transaction.getType().getName() + " type must have a positive amount.", new Exception());
            ((TransactionTableView) t.getTableView()).updateTransactionTableView();
            return;
        }

        if ((amountToSet > 0) && !(transaction.getType().getName().equals("Account Credit") || transaction.getType().getName().equals("Misc Credit"))) {
            setupErrorPopup("Transactions of the " + transaction.getType().getName() + " type must have a negative amount.", new Exception());
            ((TransactionTableView) t.getTableView()).updateTransactionTableView();
            return;
        }

        transaction.setAmount(amountToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction amount.", e));
        task.startTask();
        task.waitForComplete();
    }
}
