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
import ledger.user_interface.utils.InputSanitization;

/**
 * TableColumn for amounts
 */
public class AmountColumn extends TableColumn implements IUIController {

    public AmountColumn() {
        this.setCellValueFactory(new PropertyValueFactory<TransactionModel, String>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn());
        this.setOnEditCommit(this.amountEditHandler);
        this.setComparator(new AmountComparator());
    }

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<TransactionModel, String>> amountEditHandler = new EventHandler<CellEditEvent<TransactionModel, String>>() {
        @Override
        public void handle(CellEditEvent<TransactionModel, String> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String amountToSetString = t.getNewValue();
            if (InputSanitization.isInvalidAmount(amountToSetString)) {
                setupErrorPopup("Provided amount is invalid", new Exception());
                return;
            }


            if (amountToSetString.charAt(0) == '$') {
                amountToSetString = amountToSetString.substring(1);
            }

            double amountToSetDecimal = Double.parseDouble(amountToSetString);
            int amountToSet = (int) Math.round(amountToSetDecimal * 100);

            Transaction transaction = model.getTransaction();
            transaction.setAmount(amountToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction amount.", e);
            });
//                task.RegisterSuccessEvent(() -> updateTransactionTableView());
            task.startTask();
            task.waitForComplete();
        }
    };
}
