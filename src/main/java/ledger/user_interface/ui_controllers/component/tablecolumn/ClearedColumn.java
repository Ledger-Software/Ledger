package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.ClearedEventHandler;
import ledger.user_interface.utils.PendingStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TableColumn for pending
 */
public class ClearedColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public ClearedColumn() {
        this.initController(pageLoc, this, "Unable to load ClearedColumn");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Boolean>("pending"));
        ObservableList<Boolean> observableAllPending = FXCollections.observableArrayList(true, false);

        this.setCellFactory(ComboBoxTableCell.forTableColumn(new PendingStringConverter(), observableAllPending));
        this.setOnEditCommit(new ClearedEventHandler());
    }
}
