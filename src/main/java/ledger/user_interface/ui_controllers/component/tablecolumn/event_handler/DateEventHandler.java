package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.DateColumn}
 */
public class DateEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, LocalDate>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, LocalDate> t) {
        if(t.getOldValue().equals(t.getNewValue())){
            return;
        }
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        LocalDate localDateToSet = t.getNewValue();
        t.getOldValue();
        java.util.Date dateToSet = java.sql.Date.valueOf(localDateToSet);

        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Date");
            alert.setHeaderText("This change will cause the matching transfer to change as well");
            alert.setContentText("The date will change from "+t.getOldValue()+" to "+ localDateToSet+ ". Is this okay?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() != ButtonType.OK){
                ((TransactionTableView)t.getTableView()).updateTransactionTableView();
                return;
            }
        }
        transaction.setDate(dateToSet);




        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction date.", e));

        task.startTask();
        task.waitForComplete();
    }
}
