package ledger.database.storage.table;

/**
 * A table storing data regarding recurring transactions
 */
public class RecurringTransactionTable {
    public static final String TABLE_NAME = "RECURRING_TRANS";
    public static final String RECURRING_ID = "RECURRING_ID";
    public static final String RECURRING_START_DATE = "RECURRING_START_DATE";
    public static final String RECURRING_END_DATE = "RECURRING_END_DATE";
    public static final String RECURRING_FREQUENCY = "RECURRING_FREQUENCY";
    public static final String RECURRING_AMOUNT = "RECURRING_AMOUNT";
    public static final String RECURRING_ACCOUNT_ID = "RECURRING_ACCOUNT_ID";
    public static final String RECURRING_PAYEE_ID = "RECURRING_PAYEE_ID";
    public static final String RECURRING_TYPE_ID = "RECURRING_TYPE_ID";

    /**
     * Creates the String command to create the SQLite table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " + //TABLE NAME
                        "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " + //PRIMARY KEY
                        "%s BIGINT NOT NULL, " + //START DATE
                        "%s BIGINT NOT NULL, " + //END DATE
                        "%S INT NOT NULL, " + //FREQUENCY
                        "%s LONG NOT NULL, " + //AMOUNT
                        "%s INT NOT NULL, " + //ACCOUNT ID
                        "%s INT NOT NULL, " + //PAYEE ID
                        "%s INT NOT NULL, " + //TYPE_ID
                        "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, " + //ACCOUNT FOREIGN KEY CONSTRAINT
                        "FOREIGN KEY (%s) REFERENCES %s(%s), " + //PAYEE FOREIGN KEY CONSTRAINT
                        "FOREIGN KEY (%s) REFERENCES %s(%s)" + //TYPE FOREIGN KEY CONSTRAINT
                        ")", TABLE_NAME, RECURRING_ID, RECURRING_START_DATE, RECURRING_END_DATE, RECURRING_FREQUENCY, RECURRING_AMOUNT,
                RECURRING_ACCOUNT_ID, RECURRING_PAYEE_ID, RECURRING_TYPE_ID,
                RECURRING_ACCOUNT_ID, AccountTable.TABLE_NAME, AccountTable.ACCOUNT_ID,
                RECURRING_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID,
                RECURRING_TYPE_ID, TypeTable.TABLE_NAME, TypeTable.TYPE_ID
        );
    }

    /**
     * Creates the String command to create the HT table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    //Differs from the SQLite create statement by the underscore in Auto_Increment
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " + //TABLE NAME
                        "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " + //PRIMARY KEY
                        "%s BIGINT NOT NULL, " + //START DATE
                        "%s BIGINT NOT NULL, " + //END DATE
                        "%S INT NOT NULL, " + //FREQUENCY
                        "%s LONG NOT NULL, " + //AMOUNT
                        "%s INT NOT NULL, " + //ACCOUNT ID
                        "%s INT NOT NULL, " + //PAYEE ID
                        "%s INT NOT NULL, " + //TYPE_ID
                        "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, " + //ACCOUNT FOREIGN KEY CONSTRAINT
                        "FOREIGN KEY (%s) REFERENCES %s(%s), " + //PAYEE FOREIGN KEY CONSTRAINT
                        "FOREIGN KEY (%s) REFERENCES %s(%s)" + //TYPE FOREIGN KEY CONSTRAINT
                        ")", TABLE_NAME, RECURRING_ID, RECURRING_START_DATE, RECURRING_END_DATE, RECURRING_FREQUENCY, RECURRING_AMOUNT,
                RECURRING_ACCOUNT_ID, RECURRING_PAYEE_ID, RECURRING_TYPE_ID,
                RECURRING_ACCOUNT_ID, AccountTable.TABLE_NAME, AccountTable.ACCOUNT_ID,
                RECURRING_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID,
                RECURRING_TYPE_ID, TypeTable.TABLE_NAME, TypeTable.TYPE_ID
        );
    }
}
