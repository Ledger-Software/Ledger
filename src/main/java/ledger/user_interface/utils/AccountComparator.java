package ledger.user_interface.utils;

import ledger.database.entity.Account;
import ledger.database.entity.Type;

import java.util.Comparator;

/**
 * Created by Tayler How on 1/19/2017.
 */
public class AccountComparator implements Comparator<Account> {

    @Override
    public int compare(Account a1, Account a2) {
        return a1.getName().compareTo(a2.getName());
    }
}
