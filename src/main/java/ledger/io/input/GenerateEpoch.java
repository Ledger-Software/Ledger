package ledger.io.input;

import java.time.OffsetDateTime;

/**
 * Created by Jesse Shellabarger on 10/12/2016.
 */
public class GenerateEpoch {

    public static long generate(String xmlDate) {
        StringBuilder extractedDate = new StringBuilder();

        String year = xmlDate.substring(0, 4);
        String month = xmlDate.substring(4, 6);
        String day = xmlDate.substring(6, 8);
        String hours = xmlDate.substring(8, 10);
        int indexOfOpenBracket = xmlDate.indexOf("[");
        int indexOfColon = xmlDate.indexOf(":");
        int timezoneOffset = Integer.parseInt(xmlDate.substring(indexOfOpenBracket + 1, indexOfColon));
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

        dateTime.minusHours(timezoneOffset);

        return dateTime.toEpochSecond() * 1000;
    }
}
