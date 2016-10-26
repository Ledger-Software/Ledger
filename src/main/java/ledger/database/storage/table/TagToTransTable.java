package ledger.database.storage.table;

public class TagToTransTable {

    private static final String tableTagToTrans = "CREATE TABLE IF NOT EXISTS TAG_TO_TRANS " +
            "(TTTS_TAG_ID INT           NOT NULL, " +
            "TTTS_TRANS_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTTS_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTTS_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";

    public static String TABLE_NAME = "TAG_TO_TRANS";
    public static String TTTS_TAG_ID = "TTTS_TAG_ID";
    public static String TTTS_TRANS_ID = "TTTS_TRANS_ID";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                        ")", TABLE_NAME, TTTS_TAG_ID, TTTS_TRANS_ID,
                TTTS_TAG_ID, TagTable.TABLE_NAME, TagTable.TAG_ID,
                TTTS_TRANS_ID, TransactionTable.TABLE_NAME, TransactionTable.TRANS_ID
        );
    }
}
