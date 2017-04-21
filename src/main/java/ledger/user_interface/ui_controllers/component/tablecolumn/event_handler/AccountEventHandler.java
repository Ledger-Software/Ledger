package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.AccountColumn}
 */
public class AccountEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Account>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Account> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Account accountToSet = t.getNewValue();
        TransactionTableView transactionTableView = (TransactionTableView) t.getTableView();


        if(transaction.getType().getName().equals("Transfer")){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(accountToSet.equals(transaction.getTransferAccount())){
                setupErrorPopup("Source and Destination accounts can not be the same");
                transactionTableView.updateTransactionTableView();
                return;
            }
            if(transaction.getAmount()>=0) {
                alert.setTitle("Edit Transfer Destination Acconut");
                alert.setContentText("The Destination Account will change from "+transaction.getAccount().getName() +" to "+ accountToSet.getName()+ ". Is this okay?");
            }
            else {
                alert.setTitle("Edit Transfer Source Account");
                alert.setContentText("The Source Account will change from "+transaction.getAccount().getName() +" to "+ accountToSet.getName()+ ". Is this okay?");
            }
            alert.setHeaderText("This change will cause the other Transfer to change as well.");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() != ButtonType.OK){
                ((TransactionTableView)t.getTableView()).updateTransactionTableView();
                return;
            }
        }
        transaction.setAccount(accountToSet);
        TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
        task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction payee.", e));

        task.startTask();
        task.waitForComplete();
    }
}
