package ledger.database.storage.table;

import ledger.database.entity.Frequency;

import java.util.LinkedList;
import java.util.List;

/**
 * Table creation logic for the Frequency table.
 */
public class FrequencyTable {

    public static final String TABLE_NAME = "FREQUENCY";
    public static final String FREQUENCY_ID = "FREQUENCY_ID";
    public static final String FREQUENCY_NAME = "FREQUENCY_NAME";

    /**
     * Creates the string command to create this table for SQLite
     *
     * @return String for creating the SQLite table
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "%s TEXT NOT NULL, " +
            ")", TABLE_NAME, FREQUENCY_ID, FREQUENCY_NAME);
    }

    /**
     * Creates the string command to create this table for H2
     *
     * @return String for creating the H2 table
     */
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "%s TEXT NOT NULL, " +
                ")", TABLE_NAME, FREQUENCY_ID, FREQUENCY_NAME);
    }

    public static List<Frequency> getDefaultFrequencies() {
        List<Frequency> frequencies = new LinkedList();
        for (Frequency freq : Frequency.values()) {
            frequencies.add(freq);
        }
        return frequencies;
    }
}
