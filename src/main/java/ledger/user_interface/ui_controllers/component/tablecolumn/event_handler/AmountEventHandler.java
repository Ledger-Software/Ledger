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
 * Event Handler for Amount Column's Cell Edit Events.
 */
public class AmountEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Long>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Long> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Long amountToSet = t.getNewValue();
        if (amountToSet == null) {
            setupErrorPopup("Provided amount is invalid", new Exception());
            t.getTableView().refresh();
            return;
        }

        if ((amountToSet < 0) && (transaction.getType().getName().equals("Account Credit") || transaction.getType().getName().equals("Misc Credit")) && !(transaction.getType().getName().equals("Transfer"))) {
            setupErrorPopup("Transactions of the " + transaction.getType().getName() + " type must have a positive amount.", new Exception());
            t.getTableView().refresh();
            return;
        }

        if ((amountToSet > 0) && !(transaction.getType().getName().equals("Account Credit") || transaction.getType().getName().equals("Misc Credit")) && !(transaction.getType().getName().equals("Transfer"))) {
            setupErrorPopup("Transactions of the " + transaction.getType().getName() + " type must have a negative amount.", new Exception());
            t.getTableView().refresh();
            return;
        }
        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transfer Amount");
            alert.setHeaderText("This change will cause the matching transfer to change as well.");
            alert.setContentText("The amount will change from "+ String.format("%.2f", (double) transaction.getAmount()/100.0) + " to "+ String.format("%.2f", (double) amountToSet/100.0)+ ". Is this okay?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() != ButtonType.OK){
                t.getTableView().refresh();
                return;
            }
        }
        transaction.setAmount(amountToSet);

        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction amount.", e));
        task.startTask();
        task.waitForComplete();
    }
}
