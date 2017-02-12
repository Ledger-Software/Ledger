package ledger.user_interface.utils;

import ledger.database.entity.Account;

import java.util.Comparator;

/**
 * Created by Tayler How on 1/19/2017.
 */
public class AmountCreditComparator implements Comparator<Long> {

    @Override
    public int compare(Long amount1, Long amount2) {
        return (amount1 < amount2) ? 1 : (amount1 > amount2) ? -1 : 0;
    }
}
