package ledger.user_interface.utils;

import ledger.database.entity.Type;

import java.text.ParseException;
import java.util.Comparator;

/**
 * Created by Tayler How on 1/19/2017.
 */
public class AmountComparator implements Comparator<String> {

    @Override
    public int compare(String amount1, String amount2) {
        int a1 = parseAmountString(amount1);
        int a2 = parseAmountString(amount2);
        return a1 < a2 ? -1 : a1 == a2 ? 0 : 1;
    }

    private int parseAmountString(String amount) {
        double amountDecimal = Double.parseDouble(amount);
        int amountValue = (int) Math.round(amountDecimal * 100);
        return amountValue;
    }
}
