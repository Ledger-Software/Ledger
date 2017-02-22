package ledger.user_interface.utils;

import javafx.util.StringConverter;

/**
 * {@link StringConverter} for Check Numbers
 */
public class CheckNumberStringConverter extends StringConverter<Integer> {

    @Override
    public String toString(Integer checkNum) {
        String checkNumString;
        if (checkNum == null || checkNum.equals(-1))
            checkNumString = "";
        else {
            checkNumString = checkNum.toString();

            int length = checkNumString.length();
            for (int i = length; i < 4; i++) {
                checkNumString = "0" + checkNumString;
            }
        }
        return checkNumString;
    }

    @Override
    public Integer fromString(String checkNumString) {
        if (InputSanitization.isInvalidCheckNumber(checkNumString) || checkNumString.length() > 4) {
            // invalid check number
            return null;
        }
        int checkNum;
        if (checkNumString.equals("")) checkNum = -1;
        else checkNum = Integer.parseInt(checkNumString);

        return checkNum;
    }
}