package ledger.database.storage.table;

public class TagToPayeeTable {

    private static final String tableTagToPayee = "CREATE TABLE IF NOT EXISTS TAG_TO_PAYEE " +
            "(TTPE_TAG_ID INT           NOT NULL, " +
            "TTPE_PAYEE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";

    public static String TABLE_NAME = "TAG_TO_PAYEE";
    public static String TTPE_TAG_ID = "TTPE_TAG_ID";
    public static String TTPE_PAYEE_ID = "TTPE_PAYEE_ID";

    /**
     * Creates the String command to create the table for this object.
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                        "(%s INT NOT NULL, " +
                        "%s INT NOT NULL, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s), " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                        ")", TABLE_NAME, TTPE_TAG_ID, TTPE_PAYEE_ID,
                TTPE_TAG_ID, TagTable.TABLE_NAME, TagTable.TAG_ID,
                TTPE_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID
        );
    }
}
