package ledger.user_interface.utils;

import ledger.database.entity.Account;

import java.util.Comparator;

/**
 * Compares two accounts based on their primary name
 */
public class AccountComparator implements Comparator<Account> {

    @Override
    public int compare(Account a1, Account a2) {
        return a1.getName().compareTo(a2.getName());
    }
}
