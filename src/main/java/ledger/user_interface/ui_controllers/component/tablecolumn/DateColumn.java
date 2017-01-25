package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.LocalDateTableCell;

import java.time.LocalDate;
import java.util.Date;

/**
 * TableColumn for Dates
 */
public class DateColumn extends TableColumn implements IUIController {

    public DateColumn() {
        this.setCellValueFactory(new PropertyValueFactory<TransactionModel, Date>("date"));
        this.setCellFactory(column -> new LocalDateTableCell<>(this));
        this.setOnEditCommit(this.dateEditHandler);
    }

    private EventHandler<CellEditEvent<TransactionModel, LocalDate>> dateEditHandler = new EventHandler<CellEditEvent<TransactionModel, LocalDate>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, LocalDate> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            LocalDate localDateToSet = t.getNewValue();

            java.util.Date dateToSet = java.sql.Date.valueOf(localDateToSet);

            Transaction transaction = model.getTransaction();
            transaction.setDate(dateToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction date.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
