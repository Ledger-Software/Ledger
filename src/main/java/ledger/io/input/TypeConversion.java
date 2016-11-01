package ledger.io.input;

import ledger.database.entity.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class to Convert Strings back to their set Types.
 */
public class TypeConversion {

    public static final Type DEBIT_CARD = new Type("Debit Card", "");
    public static final Type CREDIT_CARD = new Type("Credit Card", "");
    public static final Type CASH = new Type("Cash", "");
    public static final Type CHECK = new Type("Check", "");
    public static final Type ACH_CREDIT = new Type("Account Credit", "");
    public static final Type ACH_DEBIT = new Type("Account Debit", "");
    public static final Type MISC_DEBIT = new Type("Misc Debit", "");
    public static final Type MISC_CREDIT = new Type("Misc Credit", "");

    public static final Type UNKNOWN = new Type("UNKNOWN", "Unknown Type of transaction");

    public static Type convert(String type) {
        switch (type) {
            case "DEBIT_CARD":
                return DEBIT_CARD;
            case "CREDIT_CARD":
                return CREDIT_CARD;
            case "CASH":
                return CASH;
            case "CHECK":
                return CHECK;
            case "ACH_CREDIT":
                return ACH_CREDIT;
            case "ACH_DEBIT":
                return ACH_DEBIT;
            case "MISC_DEBIT":
                return MISC_DEBIT;
            case "MISC_CREDIT":
                return MISC_CREDIT;
            default:
                return UNKNOWN;
        }
    }

    public static List<Type> getAllTypes() {
        List<Type> types = new ArrayList<Type>();

        types.add(DEBIT_CARD);
        types.add(CREDIT_CARD);
        types.add(CASH);
        types.add(ACH_CREDIT);
        types.add(ACH_DEBIT);
        types.add(MISC_DEBIT);
        types.add(MISC_CREDIT);
        types.add(UNKNOWN);

        return types;
    }
}
