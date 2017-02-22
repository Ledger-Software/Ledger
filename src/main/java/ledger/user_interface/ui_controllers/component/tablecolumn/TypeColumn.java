package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.TypeEventHandler;
import ledger.user_interface.utils.TypeComparator;
import ledger.user_interface.utils.TypeStringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TableColumn for Types
 */
public class TypeColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public TypeColumn() {
        this.initController(pageLoc, this, "Unable to load TypeColumn");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Type>("type"));

        TaskWithReturn<List<Type>> getAllTypesTask = DbController.INSTANCE.getAllTypes();
        getAllTypesTask.startTask();
        List<Type> allTypes = getAllTypesTask.waitForResult();

        ObservableList<Type> observableAllTypes = FXCollections.observableList(allTypes);
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new TypeStringConverter(), observableAllTypes));
        this.setOnEditCommit(new TypeEventHandler());
        this.setComparator(new TypeComparator());
    }
}
