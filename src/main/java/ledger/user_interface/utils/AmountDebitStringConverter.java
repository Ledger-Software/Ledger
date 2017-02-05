package ledger.user_interface.utils;

/**
 * Created by Tayler How on 11/1/2016.
 */
public class AmountDebitStringConverter extends AAmountStringConverter {

    @Override
    public Integer fromString(String amountString) {
        if (InputSanitization.isInvalidAmount(amountString)) {
            // invalid amount
            return null;
        }

        if (amountString.charAt(0) == '$') {
            amountString = amountString.substring(1);
        }

        double amountToSetDecimal = Double.parseDouble(amountString);
        int amount = (int) Math.round(amountToSetDecimal * 100);
        amount = -amount;
        return amount;
    }

    public String toString(Integer amount) {
        String amountString = "";
        if (amount < 0) {
            // The amount is negative; it is a debit to the account
            amount = Math.abs(amount);
            String amountInCents = String.valueOf(amount);
            String dollars = amountInCents.substring(0, amountInCents.length() - 2);
            String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
            amountString = "$" + dollars + "." + cents;
        }

        return amountString;
    }
}
