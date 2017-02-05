package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.database.entity.Type;
import ledger.exception.StorageException;
import ledger.io.input.TypeConversion;

/**
 * Created by Tayler How on 11/1/2016.
 */
public class AmountStringConverter extends StringConverter<Integer> {

    public Integer fromString(String amountString) {
        if (InputSanitization.isInvalidAmount(amountString)) {
            // invalid amount
            return null;
        }

        double amountToSetDecimal = Double.parseDouble(amountString);
        int amount = (int) Math.round(amountToSetDecimal * 100);
        return amount;
    }

    public String toString(Integer amount) {
        String amountInCents = String.valueOf(amount);
        String dollars = amountInCents.substring(0, amountInCents.length() - 2);
        String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
        String amountString = dollars + "." + cents;

        return amountString;
    }
}
