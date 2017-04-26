package ledger.user_interface.ui_controllers.component.tablecolumn.CustomCells;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.StringConverter;
import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.user_interface.utils.PayeeComboBoxConverter;

/**
 * This is the custom cell for the Payee column.
 * It handles the UI logic for Transfer account display.
 */
public class PayeeComboBoxTableCell extends ComboBoxTableCell<Transaction, IEntity> {

    private ObservableList<Account> accounts;
    private ObservableList<Payee> payees;

    private ObjectProperty<StringConverter<IEntity>> converterObjectProperty;

    public PayeeComboBoxTableCell(ObservableList observableAllPayees, ObservableList observableAllAccounts) {
        this.accounts = observableAllAccounts;
        this.payees = observableAllPayees;

        this.converterObjectProperty = new SimpleObjectProperty<>(new PayeeComboBoxConverter());


        this.converterProperty().bindBidirectional(this.converterObjectProperty);


    }

    @Override
    public void updateItem(IEntity item, boolean empty) {

        if (empty) {

        } else if (item instanceof Account) {
            ((PayeeComboBoxConverter)this.converterObjectProperty.get()).setIsAcc(true);

            this.getItems().setAll(accounts);
        } else {
            ((PayeeComboBoxConverter)this.converterObjectProperty.get()).setIsAcc(false);
            this.getItems().setAll(payees);
        }
        super.updateItem(item, empty);
    }
}
