package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;

import java.util.Calendar;
import java.util.function.BiConsumer;

public class CalendarEventHandler implements javafx.event.EventHandler<javafx.scene.control.TableColumn.CellEditEvent<ledger.database.entity.RecurringTransaction, java.util.Calendar>>, IUIController {

    private final BiConsumer<RecurringTransaction, Calendar> setter;

    public CalendarEventHandler(BiConsumer<RecurringTransaction, Calendar> setter) {
        this.setter = setter;
    }

    @Override
    public void handle(TableColumn.CellEditEvent<RecurringTransaction, Calendar> t) {
        RecurringTransaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Calendar calendar = t.getNewValue();

        setter.accept(transaction,calendar);

        TaskNoReturn task = DbController.INSTANCE.editRecurringTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction date.", e));

        task.startTask();
        task.waitForComplete();
    }
}
