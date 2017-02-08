package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * Created by Tayler How on 2/5/2017.
 */
public abstract class AAmountStringConverter extends StringConverter<Integer> {

    public Integer fromString(String amountString) {
        if (InputSanitization.isInvalidAmount(amountString)) {
            // invalid amount
            return null;
        }

        double amountToSetDecimal = Double.parseDouble(amountString);
        int amount = (int) Math.round(amountToSetDecimal * 100);
        return amount;
    }

    public abstract String toString(Integer amount);
}
