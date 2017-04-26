package ledger.user_interface.utils;

import ledger.database.entity.Account;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;

import java.util.Comparator;

/**
 * {@link Comparator} for {@link Payee} and {@link Account} that does it based on name for the mixed table cell.
 */
public class PayeeComboboxComparator  implements Comparator<IEntity> {
    @Override
    public int compare(IEntity o1, IEntity o2) {
        if(o1 instanceof Account){
            if(o2 instanceof Account)
                return ((Account) o1).getName().compareTo(((Account) o2).getName());
            return ((Account) o1).getName().compareTo(((Payee) o2).getName());

        } else {
            if(o2 instanceof Account)
                return ((Payee) o1).getName().compareTo(((Account) o2).getName());
            return ((Payee) o1).getName().compareTo(((Payee) o2).getName());
        }
    }
}
