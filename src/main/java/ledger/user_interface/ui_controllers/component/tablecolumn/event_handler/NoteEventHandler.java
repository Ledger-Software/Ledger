package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.NoteColumn}
 */
public class NoteEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Transaction>>, IUIController {
    @Override
    public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, Transaction> t) {
       Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Transaction transactiontoSet = t.getNewValue();
        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Note");
            alert.setHeaderText("This change will cause the matching transfer to change as well");
            alert.setContentText("The notes will change to: " + transactiontoSet.getNote().getNoteText());
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() != ButtonType.OK){
                ((TransactionTableView)t.getTableView()).updateTransactionTableView();
                return;
            }
        }

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transactiontoSet);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction note.", e));

        task.startTask();
        task.waitForComplete();
    }
}
