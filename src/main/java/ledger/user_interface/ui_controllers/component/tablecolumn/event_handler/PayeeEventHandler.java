package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.PayeeColumn}
 */
public class PayeeEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, IEntity>>, IUIController {
    @Override
    public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, IEntity> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        if(t.getNewValue() instanceof Payee) {
            Payee payeeToSet = (Payee)t.getNewValue();

            transaction.setPayee(payeeToSet);

            TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction payee.", e));

            task.startTask();
            task.waitForComplete();
        } else if (t.getNewValue() instanceof Account) {
            Account accountToSet = (Account)t.getNewValue();
            if(accountToSet.equals(transaction.getAccount())){
                setupErrorPopup("Source and Destination accounts can not be the same");

                t.getTableView().refresh();
                return;
            }
            transaction.setTransferAccount(accountToSet);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(transaction.getAmount()>=0) {
                alert.setTitle("Edit Transfer Destination Acconut");
                alert.setContentText("The Destination Account will change from "+transaction.getTransferAccount().getName() +" to "+ accountToSet.getName()+ ". Is this okay?");
            }
            else {
                alert.setTitle("Edit Transfer Source Account");
                alert.setContentText("The Source Account will change from "+transaction.getTransferAccount().getName() +" to "+ accountToSet.getName()+ ". Is this okay?");
            }
            alert.setHeaderText("This change will cause the other Transfer to change as well.");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() != ButtonType.OK){
                ((TransactionTableView)t.getTableView()).updateTransactionTableView();
                return;
            }
            TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction transfer account.", e));

            task.startTask();
            task.waitForComplete();
        }
    }
}
