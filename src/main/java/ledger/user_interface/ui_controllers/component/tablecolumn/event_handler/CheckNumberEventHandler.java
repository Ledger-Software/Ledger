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

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.CheckNumberColumn}
 */
public class CheckNumberEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Integer>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Integer> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Integer checkNumberToSet = t.getNewValue();
        TransactionTableView transactionTableView = (TransactionTableView) t.getTableView();

        if (checkNumberToSet == null) {
            setupErrorPopup("Provided check number is invalid", new Exception());
            ((TransactionTableView) t.getTableView()).updateTransactionTableView();
            return;
        }
        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Check Number");
            alert.setHeaderText("This change will cause the matching transfer to change as well.");
            if(transaction.getCheckNumber() ==-1){
                alert.setContentText("The check number will be set to " + checkNumberToSet + ". Is this okay?");
            } else {
                alert.setContentText("The check number will change from " + transaction.getCheckNumber() + " to " + checkNumberToSet + ". Is this okay?");
            }
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() != ButtonType.OK){
                ((TransactionTableView)t.getTableView()).updateTransactionTableView();
                return;
            }
        }
        transaction.setCheckNumber(checkNumberToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction amount.", e));
        task.startTask();
        task.waitForComplete();
    }
}
