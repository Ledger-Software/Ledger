package ledger.database.storage.table;

public class PayeeTable {

    public static final String TABLE_NAME = "PAYEE";
    public static final String PAYEE_ID = "PAYEE_ID";
    public static final String PAYEE_NAME = "PAYEE_NAME";
    public static final String PAYEE_DESC = "PAYEE_DESC";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, PAYEE_ID, PAYEE_NAME, PAYEE_DESC);
    }

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, PAYEE_ID, PAYEE_NAME, PAYEE_DESC);
    }
}
