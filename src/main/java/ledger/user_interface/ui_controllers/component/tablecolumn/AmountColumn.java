package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.AmountComparator;
import ledger.user_interface.utils.AmountStringConverter;
import ledger.user_interface.utils.InputSanitization;

/**
 * TableColumn for amounts
 */
public class AmountColumn extends TableColumn implements IUIController {

    public AmountColumn() {
        this.setCellValueFactory(new PropertyValueFactory<TransactionModel, Integer>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountStringConverter()));
        this.setOnEditCommit(this.amountEditHandler);
    }

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<TransactionModel, Integer>> amountEditHandler = new EventHandler<CellEditEvent<TransactionModel, Integer>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, Integer> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Integer amountToSet = t.getNewValue();
            if (amountToSet == null) {
                setupErrorPopup("Provided amount is invalid", new Exception());
                return;
            }

            Transaction transaction = model.getTransaction();
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
