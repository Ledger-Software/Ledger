package ledger.user_interface.utils;

/**
 * Created by Tayler How on 11/1/2016.
 */
public class AmountCreditStringConverter extends AAmountStringConverter {

    public String toString(Integer amount) {
        String amountString = "";
        if (amount > 0) {
            // The amount is positive; it is a credit to the account
            String amountInCents = String.valueOf(amount);
            String dollars = amountInCents.substring(0, amountInCents.length() - 2);
            String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
            amountString = dollars + "." + cents;
        }

        return amountString;
    }
}
