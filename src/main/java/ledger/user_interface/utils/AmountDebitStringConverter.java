package ledger.user_interface.utils;

/**
 * {@link javafx.util.StringConverter} that's toString formats the amount into a proper
 * debit currency amount string.
 */
public class AmountDebitStringConverter extends AAmountStringConverter {

    @Override
    public Long fromString(String amountString) {
        if (InputSanitization.isInvalidAmount(amountString)) {
            // invalid amount
            return null;
        }

        double amountToSetDecimal = Double.parseDouble(amountString);
        long amount = Math.round(amountToSetDecimal * 100);
        amount = -amount;
        return amount;
    }

    public String toString(Long amount) {
        if (amount == null) {
            return "";
        }

        String amountString = "";
        if (amount < 0) {
            Long absoluteAmount = Math.abs(amount);
            String absoluteAmountInCents = String.valueOf(absoluteAmount);

            String dollars;
            String cents;
            if (absoluteAmountInCents.length() > 2) {
                dollars = absoluteAmountInCents.substring(0, absoluteAmountInCents.length() - 2);
                cents = absoluteAmountInCents.substring(absoluteAmountInCents.length() - 2, absoluteAmountInCents.length());
            } else if (absoluteAmountInCents.length() == 2) {
                dollars = "0";
                cents = absoluteAmountInCents;
            } else {
                dollars = "0";
                cents = "0" + absoluteAmountInCents;
            }

            amountString = dollars + "." + cents;

            return amountString;
        }

        return amountString;
    }
}
