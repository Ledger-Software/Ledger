package ledger.io.input;

import ledger.database.entity.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class to Convert Strings back to their set Types.
 */
public class TypeConversion {

    public static final Type DEBIT_CARD = new Type("Debit Card", "");
    public static final Type ACH_CREDIT = new Type("Account Credit", "");
    public static final Type MISC_DEBIT = new Type("Misc Debit", "");

    public static final Type UNKNOWN = new Type("UNKNOWN", "Unknown Type of transaction");

    public static Type convert(String type) {
        switch (type) {
            case "DEBIT_CARD":
                return DEBIT_CARD;
            case "ACH_CREDIT":
                return ACH_CREDIT;
            case "MISC_DEBIT":
                return MISC_DEBIT;
            default:
                return UNKNOWN;
        }
    }

    public static List<Type> getAllTypes() {
        List<Type> types = new ArrayList<Type>();

        types.add(DEBIT_CARD);
        types.add(ACH_CREDIT);
        types.add(MISC_DEBIT);

        return types;
    }
}
