package ledger.user_interface.utils;

import ledger.database.entity.Payee;

import java.util.Comparator;

/**
 * {@link Comparator} for {@link Payee} that does it based on name.
 */
public class PayeeComparator implements Comparator<Payee> {

    @Override
    public int compare(Payee p1, Payee p2) {
        return p1.getName().compareTo(p2.getName());
    }
}
