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
public class CheckNumberColumn extends TableColumn implements IUIController {

    public CheckNumberColumn() {

        // TODO: change this to fill with Transaction's check # property
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
        // TODO: implement CheckNumberStringConverter if necessary
//        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountStringConverter()));
        this.setOnEditCommit(this.checkNumberEditHandler);
    }

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<Transaction, Integer>> checkNumberEditHandler = new EventHandler<CellEditEvent<Transaction, Integer>>() {
        @Override
        public void handle(CellEditEvent<Transaction, Integer> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Integer checkNumberToSet = t.getNewValue();

            System.out.println(checkNumberToSet);

            // TODO: Null check may be necessary if a String converter is used. What happens if the number is invalid
//            if (checkNumberToSet == null) {
//                setupErrorPopup("Provided check number is invalid", new Exception());
//                TransactionTableView transactionTableView = (TransactionTableView) getTableView();
//                transactionTableView.updateTransactionTableView();
//                return;
//            }

            // TODO: update with transaction.setCheckNumber()
//            transaction.setAmount(checkNumberToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction amount.", e);
            });
            task.startTask();
            task.waitForComplete();
        }
    };
}
