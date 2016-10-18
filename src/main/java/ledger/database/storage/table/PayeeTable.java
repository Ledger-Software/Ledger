package ledger.database.storage.table;

public class PayeeTable {

    private static final String tablePayee = "CREATE TABLE IF NOT EXISTS PAYEE " +
            "(PAYEE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "PAYEE_NAME TEXT           NOT NULL, " +
            "PAYEE_DESC TEXT           NOT NULL" +
            ")";

    public static String TABLE_NAME = "PAYEE";
    public static String PAYEE_ID = "PAYEE_ID";
    public static String PAYEE_NAME = "PAYEE_NAME";
    public static String PAYEE_DESC = "PAYEE_DESC";

    /**
     * Creates the String command to create the table for this object.
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, PAYEE_ID, PAYEE_NAME, PAYEE_DESC);
    }
}
