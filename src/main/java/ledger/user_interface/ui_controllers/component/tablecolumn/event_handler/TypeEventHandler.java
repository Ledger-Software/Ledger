package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.TypeColumn}
 */
public class TypeEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Type>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Type> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Type typeToSet = t.getNewValue();

        transaction.setType(typeToSet);

        TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction type.", e));

        task.startTask();
        task.waitForComplete();
    }
}
