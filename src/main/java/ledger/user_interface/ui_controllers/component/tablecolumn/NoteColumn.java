package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.NoteEditInputController;
import org.controlsfx.control.table.TableRowExpanderColumn;

/**
 * Created by gert on 2/7/17.
 */
public class NoteColumn extends TableColumn implements IUIController {

    public static TableRowExpanderColumn  noteColumn(){
        TableRowExpanderColumn<Transaction> expanderColumn = new TableRowExpanderColumn<>(param->{
            NoteEditInputController noteEditInputController = new NoteEditInputController();
            noteEditInputController.setTableRowData(param);
            return noteEditInputController;
        });
        expanderColumn.setText("Note");
        expanderColumn.setCellFactory(param -> new CollapseExpandButton<>(expanderColumn));
        return expanderColumn;

    }
    private static class CollapseExpandButton<Transaction> extends TableCell<Transaction, Boolean> {
        private Button button = new Button();

        CollapseExpandButton(TableRowExpanderColumn<Transaction> column) {
            button.setOnAction(event -> column.toggleExpanded(getIndex()));

        }

        protected void updateItem(Boolean expanded, boolean empty) {
            super.updateItem(expanded, empty);
            if (expanded == null || empty) {
                setGraphic(null);
            } else {
                button.setText(expanded ? "Collapse Note " : "Expand Note");
                setGraphic(button);
            }
        }
    }
}
