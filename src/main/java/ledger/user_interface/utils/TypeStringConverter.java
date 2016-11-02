package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.database.entity.Type;
import ledger.io.input.TypeConversion;

/**
 * Created by Tayler How on 11/1/2016.
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
