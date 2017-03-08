package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

import java.time.LocalDate;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.DateColumn}
 */
public class DateEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, LocalDate>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, LocalDate> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        LocalDate localDateToSet = t.getNewValue();

        java.util.Date dateToSet = java.sql.Date.valueOf(localDateToSet);

        transaction.setDate(dateToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction date.", e));

        task.startTask();
        task.waitForComplete();
    }
}
