package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * Converter from String to boolean for the isMatch of an ignoreExpression
 */
public class IsMatchConverter extends StringConverter<Boolean> {

    public Boolean fromString(String matchString) {
        return matchString.equals("Matches");
    }

    public String toString(Boolean match) {
        // convert a Type instance to the text displayed in the choice box
        if (match) {
            return "Matches";
        } else {
            return "Contains";
        }
    }
}
