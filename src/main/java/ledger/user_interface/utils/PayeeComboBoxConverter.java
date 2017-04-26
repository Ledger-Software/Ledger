package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;

/**
 * {@link StringConverter} for Payees and accounts for the mixed column
 */
public class PayeeComboBoxConverter extends StringConverter<IEntity> {
    private AccountStringConverter accountStringConverter;
    private PayeeStringConverter payeeStringConverter;
    private boolean isAccConverter;
    public PayeeComboBoxConverter() {
        accountStringConverter = new AccountStringConverter();
        payeeStringConverter = new PayeeStringConverter();
        isAccConverter = false;
    }
    @Override
    public String toString(IEntity object) {
        if(isAccConverter)
            return accountStringConverter.toString((Account)object);
        else
            return payeeStringConverter.toString((Payee) object);
    }

    @Override
    public IEntity fromString(String string) {
        if(isAccConverter)
            return accountStringConverter.fromString(string);
        else
            return payeeStringConverter.fromString(string);
    }
    public void setIsAcc(boolean isAcc){
        isAccConverter = isAcc;
    }
}
