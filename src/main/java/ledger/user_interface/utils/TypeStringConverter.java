package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.database.entity.Type;
import ledger.io.input.TypeConversion;

/**
 * {@link StringConverter} for {@link Type}
 */
public class TypeStringConverter extends StringConverter<Type> {

    public Type fromString(String typeName) {
        return TypeConversion.convertName(typeName);
    }

    public String toString(Type type) {
        // convert a Type instance to the text displayed in the choice box
        if (type != null) {
            return type.getName();
        } else {
            return "";
        }
    }
}
