package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.PendingStringConverter;

/**
 * TableColumn for pending
 */
public class ClearedColumn extends TableColumn implements IUIController {

    public ClearedColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Boolean>("pending"));
        ObservableList<Boolean> observableAllPending = FXCollections.observableArrayList(true, false);

        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PendingStringConverter(), observableAllPending));
        this.setOnEditCommit(this.closedEditHandler);
    }

    private EventHandler<CellEditEvent<Transaction, Boolean>> closedEditHandler = new EventHandler<CellEditEvent<Transaction, Boolean>>() {
        @Override
        public void handle(CellEditEvent<Transaction, Boolean> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            boolean pendingToSet = t.getNewValue();

            transaction.setPending(pendingToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction pending field.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
