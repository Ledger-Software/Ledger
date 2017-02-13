package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.utils.PayeeComparator;
import ledger.user_interface.utils.PayeeStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TableColumn for Payees
 */
public class PayeeColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public PayeeColumn() {
        this.initController(pageLoc, this, "Unable to load PayeeColumn");
    }

    ObservableList observableAllPayees;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();
        this.observableAllPayees = FXCollections.observableList(allPayees);

        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), this.observableAllPayees));
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Payee>("payee"));
        this.setOnEditCommit(this.payeeEditHandler);
        this.setComparator(new PayeeComparator());

        DbController.INSTANCE.registerPayeeSuccessEvent(() -> Startup.INSTANCE.runLater(this::updatePayeeList));
    }

    private void updatePayeeList() {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();

        for (Payee currentPayee : allPayees) {
            if (!observableAllPayees.contains(currentPayee)) {
                observableAllPayees.add(currentPayee);
            }
        }
    }

    private EventHandler<javafx.scene.control.TableColumn.CellEditEvent<Transaction, Payee>> payeeEditHandler = new EventHandler<javafx.scene.control.TableColumn.CellEditEvent<Transaction, Payee>>() {
        @Override
        public void handle(javafx.scene.control.TableColumn.CellEditEvent<Transaction, Payee> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Payee payeeToSet = t.getNewValue();

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
