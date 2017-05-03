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
import ledger.user_interface.utils.PendingStringConverter;

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.ClearedColumn}
 */
public class ClearedEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Boolean>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Boolean> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        boolean pendingToSet = t.getNewValue();

        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Cleared Status");
            PendingStringConverter converter = new PendingStringConverter();
            alert.setHeaderText("This change will cause the matching transfer to change as well.");
            alert.setContentText("The pending value will change from "+ converter.toString(transaction.isPending()) +" to "+  converter.toString(pendingToSet)+ ". Is this okay?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() != ButtonType.OK){
                t.getTableView().refresh();
                return;
            }
        }
        transaction.setPending(pendingToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction pending field.", e));

        task.startTask();
        task.waitForComplete();
    }
}
