package ledger.user_interface.utils;

import java.util.regex.Pattern;

/**
 * Class to hold the front-ends input sanitization verification
 */
public class InputSanitization {

    private static final String Digits     = "(\\p{Digit}+)";
    private static final String HexDigits  = "(\\p{XDigit}+)";
    private static final String Exp        = "[eE][+-]?"+Digits;
    private static final String fpRegex    =
            ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
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
                    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\.("+Digits+")("+Exp+")?)|"+

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                    ")[pP][+-]?" + Digits + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*");// Optional trailing "whitespace"

    public static boolean isStringInvalid(String str){
        return "".equals(str) || str == null;
    }

    public static boolean isInvalidAmount(String str) {
        return !Pattern.matches(fpRegex, str);
    }
}
