package ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells;

import javafx.collections.ObservableList;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;

/**
 * This is the custom cell for the Payee column.
 * It handles the UI logic for Transfer account display.
 */
public class PayeeComboBoxTableCell extends ComboBoxTableCell<Transaction, IEntity> {
    private ObservableList<IEntity> items;
    private ObservableList<Account> accounts;
    private ObservableList<Payee> payees;
    private StringConverter payeeStringConverter;
    private StringConverter accountStringConverter;

    public PayeeComboBoxTableCell(StringConverter<Payee> payeeStringConverter, StringConverter<Account> accountStringConverter, ObservableList observableAllPayees, ObservableList observableAllAccounts, ObservableList<IEntity> items) {
        super(items);
        this.items = items;
        this.accounts = observableAllAccounts;
        this.payees = observableAllPayees;
        this.payeeStringConverter = payeeStringConverter;
        this.accountStringConverter = accountStringConverter;


    }

    @Override
    public void updateItem(IEntity item, boolean empty) {

        if (empty) {

        } else if (item instanceof Account) {
            this.converterProperty().setValue((StringConverter<IEntity>) accountStringConverter);
            items.setAll(accounts);
            setItem(item);
        } else {
            this.converterProperty().setValue((StringConverter<IEntity>) payeeStringConverter);
            items.setAll(payees);
            setItem(item);
        }
        super.updateItem(item, empty);
    }
}
