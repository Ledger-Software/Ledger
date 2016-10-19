package ledger.io.input;

import ledger.database.entity.Type;

/**
 * Utility Class to Convert Strings back to their set Types.
 */
public class TypeConversion {

    public static final Type DEBIT_CARD = new Type("Debit Card", "");
    public static final Type UNKNOWN = new Type("UNKNOWN", "Unknown Type of transaction");

    public static Type convert(String type) {
        switch (type) {
            case "DEBIT_CARD":
                return DEBIT_CARD;
            default:
                return UNKNOWN;
        }
    }
}
