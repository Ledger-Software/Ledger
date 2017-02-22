package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.PayeeEventHandler;
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

    private ObservableList observableAllPayees;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
        getAllPayeesTask.startTask();
        List<Payee> allPayees = getAllPayeesTask.waitForResult();
        this.observableAllPayees = FXCollections.observableList(allPayees);

        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PayeeStringConverter(), this.observableAllPayees));
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Payee>("payee"));
        this.setOnEditCommit(new PayeeEventHandler());
        this.setComparator(new PayeeComparator());

        DbController.INSTANCE.registerPayeeSuccessEvent((ignored) -> Startup.INSTANCE.runLater(this::updatePayeeList));
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
}
