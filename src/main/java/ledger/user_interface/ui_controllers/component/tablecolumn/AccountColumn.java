package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.AccountComparator;
import ledger.user_interface.utils.AccountStringConverter;

/**
 * TableColumn for amounts
 */
public class AccountColumn extends TableColumn implements IUIController {

    public AccountColumn() {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Account>("account"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AccountStringConverter()));
        this.setComparator(new AccountComparator());
        this.setEditable(false);
    }
}
