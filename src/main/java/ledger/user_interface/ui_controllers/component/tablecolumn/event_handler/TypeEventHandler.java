package ledger.user_interface.ui_controllers.component.tablecolumn.event_handler;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.user_interface.ui_controllers.IUIController;

import java.util.Optional;

/**
 * {@link EventHandler} for {@link ledger.user_interface.ui_controllers.component.tablecolumn.TypeColumn}
 */
public class TypeEventHandler implements EventHandler<TableColumn.CellEditEvent<Transaction, Type>>, IUIController {
    @Override
    public void handle(TableColumn.CellEditEvent<Transaction, Type> t) {
        Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Type typeToSet = t.getNewValue();

        if (transaction.getAmount() < 0 && (typeToSet.getName().equals("Account Credit") || typeToSet.getName().equals("Misc Credit"))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transaction Type");
            alert.setHeaderText("Transactions of the Account Credit/Misc Credit type must have a non-negative value.");
            alert.setContentText("To change the Transaction type to Account Credit/Misc Credit, the amount will automatically be changed to a non-negative value, is this okay?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                transaction.setAmount(-transaction.getAmount());
                transaction.setType(typeToSet);

                TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction type.", e));

                task.startTask();
                task.waitForComplete();
            } else {
                return;
            }
        } else if (transaction.getAmount() > 0 && !(typeToSet.getName().equals("Account Credit") || typeToSet.getName().equals("Misc Credit"))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit Transaction Type");
            alert.setHeaderText("Transactions not of the Account Credit/Misc Credit type cannot have a positive value.");
            alert.setContentText("To change the Transaction type from Account Credit/Misc Credit, the amount will automatically be changed to a negative value, is this okay?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                transaction.setAmount(-transaction.getAmount());
                transaction.setType(typeToSet);

                TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
                task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction type.", e));

                task.startTask();
                task.waitForComplete();
            } else {
                return;
            }
        } else {
            transaction.setType(typeToSet);

            TaskNoReturn task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> setupErrorPopup("Error editing transaction type.", e));

            task.startTask();
            task.waitForComplete();
        }
    }
}
