package ledger.user_interface.utils;

/**
 * Created by Tayler How on 11/1/2016.
 */

public class AmountStringConverter extends AAmountStringConverter {

    public String toString(Integer amount) {
        String amountInCents = String.valueOf(amount);

        String amountString;
        if (amount > 100) {
            String dollars = amountInCents.substring(0, amountInCents.length() - 2);
            String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
            amountString = dollars + "." + cents;
        } else {
            amountString = "0." + (amountInCents.length() == 1 ? "0" : "") + amountInCents;
        }
        return amountString;
    }
}
