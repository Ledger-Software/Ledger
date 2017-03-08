package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * String Converter for showing valid currency amounts. Only has From String implemented
 */
public abstract class AAmountStringConverter extends StringConverter<Long> {

    public Long fromString(String amountString) {
        if (InputSanitization.isInvalidAmount(amountString)) {
            // invalid amount
            return null;
        }

        double amountToSetDecimal = Double.parseDouble(amountString);

        return Math.round(amountToSetDecimal * 100);
    }

    public abstract String toString(Long amount);
}
