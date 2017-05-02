package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.NoteEditInputController;
import ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells.NoteColumnTableCell;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.NoteEventHandler;
import ledger.user_interface.utils.IdentityCellValueCallback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * {@link TableColumn} that handles Note text for {@link Transaction} objects
 */
public class NoteColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";


    public NoteColumn() {
        this.initController(pageLoc, this, "Unable to load NoteColumn");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        this.setCellValueFactory(new IdentityCellValueCallback<>());
        this.setCellFactory(new Callback<TableColumn<Transaction, Transaction>, TableCell<Transaction, Transaction>>() {
            @Override
            public TableCell<Transaction, Transaction> call(TableColumn<Transaction, Transaction> param) {
                return new NoteColumnTableCell();
            }
        });
        this.setOnEditCommit(new NoteEventHandler());
        this.setSortable(false);
    }

}
