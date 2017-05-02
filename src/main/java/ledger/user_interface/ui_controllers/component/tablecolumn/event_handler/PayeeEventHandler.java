package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.PayeeColumn}
 */
public class PayeeEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Payee>>, IUIController {
    @Override
    public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, Payee> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Payee payeeToSet = t.getNewValue();

        transaction.setPayee(payeeToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction payee.", e));
        
        task.startTask();
        task.waitForComplete();
    }
}
