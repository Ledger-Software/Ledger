package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Frequency;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;

public class FrequencyEventHandler implements IUIController, javafx.event.EventHandler<javafx.scene.control.TableColumn.CellEditEvent<ledger.database.entity.RecurringTransaction, ledger.database.entity.Frequency>> {

    @Override
    public void handle(TableColumn.CellEditEvent<RecurringTransaction, Frequency> t) {
        RecurringTransaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());

        transaction.setFrequency(t.getNewValue());

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction date.", e));
        task.startTask();
        task.waitForComplete();
    }
}
