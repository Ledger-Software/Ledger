package ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells;

import javafx.collections.ObservableList;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.io.input.TypeConversion;


/**
 * This is the custom cell for the Type column.
 * It handles the UI logic for making the type of transfer transactions uneditable.
 */
public class TypeComboBoxTableCell extends ComboBoxTableCell<Transaction, Type> {
    ;

    public TypeComboBoxTableCell(StringConverter<Type> converter, ObservableList<Type> items) {
        super(converter, items);


    }

    @Override
    public void updateItem(Type item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {

        } else if (item.equals(TypeConversion.ACC_TRANSFER)) {
            this.setEditable(false);
        }
    }
}
