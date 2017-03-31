package ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by gert on 3/31/17.
 */
public class NoteTextTableCell extends TableCell<Transaction, Note> {
    private TextArea noteText;


}
