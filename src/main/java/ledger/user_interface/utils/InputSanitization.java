package ledger.user_interface.utils;

import ledger.database.entity.Payee;

import java.util.regex.Pattern;

/**
 * Class to hold the front-ends input sanitization verification
 */
public class InputSanitization {

    private static final String Digits = "(\\p{Digit}+)";
    private static final String HexDigits = "(\\p{XDigit}+)";
    private static final String Exp = "[eE][+-]?" + Digits;
    private static final String fpRegex =
            ("[$]?[\\x00-\\x20]*" + // Optional leading "whitespace"
                    "[+-]?(" +         // Optional sign character

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from the Java Language Specification, 2nd
                    // edition, section 3.10.2.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.(" + Digits + ")(" + Exp + ")?)|" +

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    /**
     * Checks to see if a string is valid input to the application. The string is required to not be empty or null
     *
     * @param str The string to check for validity.
     * @return True if the string is invalid, false otherwise.
     */
    public static boolean isStringInvalid(String str) {
        return "".equals(str) || str == null;
    }

    /**
     * Checks to see if a string is valid input to the application and is able to be parsed. The string must be able
     * to be parsed as a Double.
     *
     * @param str The string to check for validity.
     * @return True if the string is invalid, false otherwise.
     */
    public static boolean isInvalidAmount(String str) {
        return !Pattern.matches(fpRegex, str);
    }
    public static boolean isInvalidCheckNumber(String str) { return !Pattern.matches(Digits,str);}
    public static boolean isInvalidPayee(Object o) {
        if (o == null) return true;
        if (!(o instanceof Payee)) return true;
        if (isStringInvalid(((Payee) o).getName())) return true;
        return false;
    }

}
