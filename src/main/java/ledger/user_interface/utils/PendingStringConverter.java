package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * {@link StringConverter} for Pending field of {@link ledger.database.entity.Account}
 */
public class PendingStringConverter extends StringConverter<Boolean> {

    public Boolean fromString(String pendingString) {
        return pendingString.equals("Pending");
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
