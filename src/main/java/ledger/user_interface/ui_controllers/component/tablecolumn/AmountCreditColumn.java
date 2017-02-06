package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.database.entity.Transaction;
import ledger.user_interface.utils.AmountCreditComparator;
import ledger.user_interface.utils.AmountCreditStringConverter;

/**
 * TableColumn for amounts
 */
public class AmountCreditColumn extends AAmountColumn {

    public AmountCreditColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountCreditStringConverter()));
        this.setComparator(new AmountCreditComparator());
        this.setOnEditCommit(this.amountEditHandler);
    }
}
