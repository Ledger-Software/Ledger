package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.NoteColumn}
 */
public class NoteEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Note>>, IUIController {
    @Override
    public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, Note> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Note noteToSet = t.getNewValue();

        transaction.setNote(noteToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction note.", e));

        task.startTask();
        task.waitForComplete();
    }
}
