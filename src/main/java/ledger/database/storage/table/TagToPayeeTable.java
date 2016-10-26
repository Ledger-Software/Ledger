package ledger.database.storage.table;

public class TagToPayeeTable {

    public static String TABLE_NAME = "TAG_TO_PAYEE";
    public static String TTPE_TAG_ID = "TTPE_TAG_ID";
    public static String TTPE_PAYEE_ID = "TTPE_PAYEE_ID";

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
                        "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                        ")", TABLE_NAME, TTPE_TAG_ID, TTPE_PAYEE_ID,
                TTPE_TAG_ID, TagTable.TABLE_NAME, TagTable.TAG_ID,
                TTPE_PAYEE_ID, PayeeTable.TABLE_NAME, PayeeTable.PAYEE_ID
        );
    }

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return CreateStatementSQLite(); //The same in this case
    }
}
