package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * Created by gert on 2/7/17.
 */
public class MatchOrContainsStringConverter extends StringConverter<Boolean> {

    public Boolean fromString(String pendingString) {
        if (pendingString.equals("Matches")) {
            return Boolean.TRUE;
        } else if(pendingString.isEmpty()) {
            return null;
        } else {
            return Boolean.FALSE;
        }
    }

    public String toString(Boolean pending) {
        // convert a Type instance to the text displayed in the choice box
        if (pending==null){
            return "";
        }
        else if (pending.equals(Boolean.TRUE)) {
            return "Matches";
        } else {
            return "Contains";
        }
    }
}
