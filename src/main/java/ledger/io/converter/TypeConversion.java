package ledger.io.converter;

import ledger.database.enity.Type;

/**
 * Created by CJ on 10/11/2016.
 */
public class TypeConversion {

    public static final Type DEBIT_CARD = new Type("Debit Card", "");
    public static final Type UNKNOWN = new Type("UNKNOWN", "Unknown Type of transaction");

    public static Type convert(String type) {
        switch(type) {
            case "DEBIT_CARD":
                return DEBIT_CARD;
            default:
                 return UNKNOWN;
        }
    }
}
