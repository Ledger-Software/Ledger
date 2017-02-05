package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;

/**
 * Created by Tayler How on 2/5/2017.
 */
public abstract class AAmountColumn extends TableColumn implements IUIController  {
    // Transaction table edit event handler
    protected EventHandler<CellEditEvent<Transaction, Integer>> amountEditHandler = new EventHandler<CellEditEvent<Transaction, Integer>>() {
        @Override
        public void handle(CellEditEvent<Transaction, Integer> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Integer amountToSet = t.getNewValue();
            if (amountToSet == null) {
                setupErrorPopup("Provided amount is invalid", new Exception());
                TransactionTableView transactionTableView = (TransactionTableView) getTableView();
                transactionTableView.updateTransactionTableView();
                return;
            }

            transaction.setAmount(amountToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction amount.", e);
            });
            task.startTask();
            task.waitForComplete();
        }
    };
}
