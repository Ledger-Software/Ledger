package ledger.database.storage.table;

public class TagToTransTable {

    public static String TABLE_NAME = "TAG_TO_TRANS";
    public static String TTTS_TAG_ID = "TTTS_TAG_ID";
    public static String TTTS_TRANS_ID = "TTTS_TRANS_ID";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE" +
                        ")", TABLE_NAME, TTTS_TAG_ID, TTTS_TRANS_ID,
                TTTS_TAG_ID, TagTable.TABLE_NAME, TagTable.TAG_ID,
                TTTS_TRANS_ID, TransactionTable.TABLE_NAME, TransactionTable.TRANS_ID
        );
    }

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return CreateStatementSQLite(); // the same in this case
    }
}
