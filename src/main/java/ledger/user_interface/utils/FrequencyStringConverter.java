package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.database.entity.Frequency;
import ledger.database.entity.Type;

public class FrequencyStringConverter extends StringConverter<Frequency>  {

    /**
     * Converts the object provided into its string form.
     * Format of the returned string is defined by the specific converter.
     *
     * @param object
     * @return a string representation of the object passed in.
     */
    @Override
    public String toString(Frequency object) {
        return Frequency.convertFrequencyToString(object);
    }

    /**
     * Converts the string provided into an object defined by the specific converter.
     * Format of the string and type of the resulting object is defined by the specific converter.
     *
     * @param string to be converted
     * @return an object representation of the string passed in.
     */
    @Override
    public Frequency fromString(String string) {
        return Frequency.convertStringToFrequency(string);
    }
}
