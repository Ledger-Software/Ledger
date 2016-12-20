package ledger.io.input;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * Generates the UNIX epoch from a yyyymmddhhmmss timestamp
 **/
public class GenerateEpoch {

    public static long generate(String xmlDate) {
        try {
            StringBuilder extractedDate = new StringBuilder();

            String year = xmlDate.substring(0, 4);
            String month = xmlDate.substring(4, 6);
            String day = xmlDate.substring(6, 8);
            String hours = xmlDate.substring(8, 10);
            String minutes = xmlDate.substring(10, 12);
            String seconds = xmlDate.substring(12, 14);

            extractedDate.append(year);
            extractedDate.append("-");
            extractedDate.append(month);
            extractedDate.append("-");
            extractedDate.append(day);
            extractedDate.append("T");
            extractedDate.append(hours);
            extractedDate.append(":");
            extractedDate.append(minutes);
            extractedDate.append(":");
            extractedDate.append(seconds);
            extractedDate.append("+00:00");

            OffsetDateTime dateTime = OffsetDateTime.parse(extractedDate.toString());

            if (xmlDate.matches(".*\\[.*:.*\\].*")) {
                int indexOfOpenBracket = xmlDate.indexOf("[");
                int indexOfColon = xmlDate.indexOf(":");
                int timezoneOffset = Integer.parseInt(xmlDate.substring(indexOfOpenBracket + 1, indexOfColon));

                dateTime.minusHours(timezoneOffset);
            }

            return dateTime.toEpochSecond() * 1000;
        } catch (StringIndexOutOfBoundsException e) {
            throw new DateTimeParseException("Date Time could not be parsed from provided string", xmlDate, xmlDate.length(), e);
        }
    }
}
