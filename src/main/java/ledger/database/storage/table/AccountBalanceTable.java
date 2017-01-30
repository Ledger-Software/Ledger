package ledger.database.storage.table;

public class AccountBalanceTable {

    public static String TABLE_NAME = "ACCOUNT_BALANCE";
    public static String ABAL_ID = "ABAL_ID";
    public static String ABAL_ACCOUNT_ID = "ABAL_ACCOUNT_ID";
    public static String ABAL_DATETIME = "ABAL_DATETIME";
    public static String ABAL_AMOUNT = "ABAL_AMOUNT";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "(%s INT NOT NULL, " +
                "%s LONG NOT NULL, " +
                "%s INT NOT NULL" +
                ")", TABLE_NAME, ABAL_ID, ABAL_ACCOUNT_ID, ABAL_DATETIME, ABAL_AMOUNT);
    }

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "%s INT NOT NULL, " +
                "%s LONG NOT NULL, " +
                "%s INT NOT NULL" +
                ")", TABLE_NAME, ABAL_ID, ABAL_ACCOUNT_ID, ABAL_DATETIME, ABAL_AMOUNT);
    }
}
