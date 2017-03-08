package ledger.user_interface.utils;

import ledger.database.entity.Account;

import java.util.Comparator;

/**
 * Used to compare the amounts of {@link Account} objects.
 */
public class AmountCreditComparator implements Comparator<Long> {

    @Override
    public int compare(Long amount1, Long amount2) {
        return (amount1 < amount2) ? 1 : (amount1 > amount2) ? -1 : 0;
    }
}
