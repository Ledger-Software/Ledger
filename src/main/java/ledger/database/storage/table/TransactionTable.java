package ledger.database.storage.table;

public class TransactionTable {

    public static final String TABLE_NAME = "TRANSACT";
    public static final String TRANS_ID = "TRANS_ID";
    public static final String TRANS_DATETIME = "TRANS_DATETIME";
    public static final String TRANS_AMOUNT = "TRANS_AMOUNT";
    public static final String TRANS_PENDING = "TRANS_PENDING";
    public static final String TRANS_ACCOUNT_ID = "TRANS_ACCOUNT_ID";
    public static final String TRANS_PAYEE_ID = "TRANS_PAYEE_ID";
    public static final String TRANS_TYPE_ID = "TRANS_TYPE_ID";
    public static final String TRANS_CHECK_NUMBER= "TRANS_CHECK_NUMBER";

    /**
     * Creates the String command to create the SQLite table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s LONG NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s BOOLEAN NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                        "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                        "%s INT NOT NULL," +
                        ")", TABLE_NAME,
                TRANS_ID, TRANS_DATETIME, TRANS_AMOUNT, TRANS_PENDING, TRANS_ACCOUNT_ID, TRANS_PAYEE_ID, TRANS_TYPE_ID,
                TRANS_ACCOUNT_ID, AccountTable.TABLE_NAME, AccountTable.ACCOUNT_ID,
                TRANS_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID,
                TRANS_TYPE_ID, TypeTable.TABLE_NAME, TypeTable.TYPE_ID, TRANS_CHECK_NUMBER
        );
    }

    /**
     * Creates the String command to create the HT table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                        "%s BIGINT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s BOOLEAN NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                        "%s INT NOT NULL" +
                        ")", TABLE_NAME,
                TRANS_ID, TRANS_DATETIME, TRANS_AMOUNT, TRANS_PENDING, TRANS_ACCOUNT_ID, TRANS_PAYEE_ID, TRANS_TYPE_ID,
                TRANS_ACCOUNT_ID, AccountTable.TABLE_NAME, AccountTable.ACCOUNT_ID,
                TRANS_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID,
                TRANS_TYPE_ID, TypeTable.TABLE_NAME, TypeTable.TYPE_ID, TRANS_CHECK_NUMBER
        );
    }
}
