package ledger.io.input;

import ledger.database.entity.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class to Convert Strings back to their set Types.
 */
public class TypeConversion {

    private static final Type DEBIT_CARD = new Type("Debit Card", "");
    private static final Type CREDIT_CARD = new Type("Credit Card", "");
    private static final Type CASH = new Type("Cash", "");
    public static final Type CHECK = new Type("Check", "");
    private static final Type ACH_CREDIT = new Type("Account Credit", "");
    private static final Type ACH_DEBIT = new Type("Account Debit", "");
    private static final Type MISC_DEBIT = new Type("Misc Debit", "");
    private static final Type MISC_CREDIT = new Type("Misc Credit", "");
    public static final Type ACC_TRANSFER = new Type("Transfer", "");

    private static final Type UNKNOWN = new Type("UNKNOWN", "Unknown Type of transaction");

    //In the case of QFX, this is where TRNTYPE value is translated
    public static Type convert(String type) {
        switch (type) {
            case "DEBIT_CARD":
                return DEBIT_CARD;
            case "POS":
            	return DEBIT_CARD;
            case "CREDIT_CARD":
                return CREDIT_CARD;
            case "CASH":
                return CASH;
            case "CHECK":
                return CHECK;
            case "ACH_CREDIT":
                return ACH_CREDIT;
            case "DIRECTDEP":
            	return ACH_CREDIT;
            case "ACH_DEBIT":
                return ACH_DEBIT;
            case "MISC_DEBIT":
                return MISC_DEBIT;
            case "DEBIT":
                return MISC_DEBIT;
            case "MISC_CREDIT":
                return MISC_CREDIT;
            case "CREDIT":
                return MISC_CREDIT;
            case "TRANSFER":
                return ACC_TRANSFER;
            default:
                return UNKNOWN;
        }
    }

    public static Type convertName(String name) {
        switch (name) {
            case "Debit Card":
                return DEBIT_CARD;
            case "Credit Card":
                return CREDIT_CARD;
            case "Cash":
                return CASH;
            case "Check":
                return CHECK;
            case "Account Credit":
                return ACH_CREDIT;
            case "Account Debit":
                return ACH_DEBIT;
            case "Misc Debit":
                return MISC_DEBIT;
            case "Misc Credit":
                return MISC_CREDIT;
            case "Transfer":
                return ACC_TRANSFER;
            default:
                return UNKNOWN;
        }
    }

    public static List<Type> getAllTypes() {
        List<Type> types = new ArrayList<>();

        types.add(DEBIT_CARD);
        types.add(CREDIT_CARD);
        types.add(CASH);
        types.add(CHECK);
        types.add(ACH_CREDIT);
        types.add(ACH_DEBIT);
        types.add(MISC_DEBIT);
        types.add(MISC_CREDIT);
        types.add(ACC_TRANSFER);
        types.add(UNKNOWN);

        return types;
    }
}
