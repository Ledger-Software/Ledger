package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * Created by gert on 2/7/17.
 */
public class MatchOrContainsStringConverter extends StringConverter<Boolean> {

    public Boolean fromString(String matchorcontiainsString) {
        if (matchorcontiainsString.equals("Matches")) {
            return true;
        } else {
            return false;
        }
    }

    public String toString(Boolean matchorcontiains) {
        // convert a Type instance to the text displayed in the choice box
        if (matchorcontiains) {
            return "Matches";
        } else {
            return "Contains";
        }
    }
}
