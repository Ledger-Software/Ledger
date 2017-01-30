package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;
import ledger.user_interface.utils.AmountStringConverter;

/**
 * TableColumn for amounts
 */
public class AmountColumn extends TableColumn implements IUIController {

    public AmountColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountStringConverter()));
        this.setOnEditCommit(this.amountEditHandler);
        this.getTableView();
    }

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<Transaction, Integer>> amountEditHandler = new EventHandler<CellEditEvent<Transaction, Integer>>() {
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
