package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.AccountColumn}
 */
public class AccountEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Account>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Account> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Account accountToSet = t.getNewValue();

        transaction.setAccount(accountToSet);

        TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction payee.", e));

        task.startTask();
        task.waitForComplete();
    }
}
