package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.PayeeComparator;
import ledger.user_interface.utils.PayeeStringConverter;

import java.util.List;

/**
 * Created by CJ on 1/23/2017.
 */
public class PayeeColumn extends TableColumn implements IUIController {

    public PayeeColumn() {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();
        ObservableList observableAllPayees = FXCollections.observableList(allPayees);

        this.setCellValueFactory(new PropertyValueFactory<TransactionModel, Payee>("payee"));
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), observableAllPayees));
        this.setOnEditCommit(this.payeeEditHandler);
        this.setComparator(new PayeeComparator());

        for (Payee currentPayee : allPayees) {
            if (!observableAllPayees.contains(currentPayee)) {
                observableAllPayees.add(currentPayee);
            }
        }
    }

    private EventHandler<javafx.scene.control.TableColumn.CellEditEvent<TransactionModel, Payee>> payeeEditHandler = new EventHandler<javafx.scene.control.TableColumn.CellEditEvent<TransactionModel, Payee>>() {
        @Override
        public void handle(javafx.scene.control.TableColumn.CellEditEvent<TransactionModel, Payee> t) {
            TransactionModel model = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Payee payeeToSet = t.getNewValue();

            Transaction transaction = model.getTransaction();
            transaction.setPayee(payeeToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction payee.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
