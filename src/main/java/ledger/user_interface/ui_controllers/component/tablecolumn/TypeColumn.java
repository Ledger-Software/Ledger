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
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.TypeComparator;
import ledger.user_interface.utils.TypeStringConverter;

import java.util.List;

/**
 * TableColumn for Types
 */
public class TypeColumn extends TableColumn implements IUIController {

    public TypeColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Type>("type"));

        TaskWithReturn<List<Type>> getAllTypesTask = DbController.INSTANCE.getAllTypes();
        getAllTypesTask.startTask();
        List<Type> allTypes = getAllTypesTask.waitForResult();

        ObservableList<Type> observableAllTypes = FXCollections.observableList(allTypes);
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new TypeStringConverter(), observableAllTypes));
        this.setOnEditCommit(this.typeEditHandler);
        this.setComparator(new TypeComparator());
    }

    private EventHandler<CellEditEvent<Transaction, Type>> typeEditHandler = new EventHandler<CellEditEvent<Transaction, Type>>() {
        @Override
        public void handle(CellEditEvent<Transaction, Type> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Type typeToSet = t.getNewValue();

            transaction.setType(typeToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction type.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
