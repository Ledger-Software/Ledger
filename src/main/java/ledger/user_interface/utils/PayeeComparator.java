package ledger.user_interface.utils;

import ledger.database.entity.Payee;
import ledger.database.entity.Type;

import java.util.Comparator;

/**
 * Created by Tayler How on 1/19/2017.
 */
public class PayeeComparator implements Comparator<Payee> {

    @Override
    public int compare(Payee p1, Payee p2) {
        return p1.getName().compareTo(p2.getName());
    }
}
