package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * Created by Tayler How on 11/1/2016.
 */
public class PendingStringConverter extends StringConverter<Boolean> {

    public Boolean fromString(String pendingString) {
        if (pendingString.equals("Pending")) {
            return true;
        } else {
            return false;
        }
    }

    public String toString(Boolean pending) {
        // convert a Type instance to the text displayed in the choice box
        if (pending) {
            return "Pending";
        } else {
            return "Cleared";
        }
    }
}
