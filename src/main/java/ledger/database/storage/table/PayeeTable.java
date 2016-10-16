package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
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

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, PAYEE_ID, PAYEE_NAME, PAYEE_DESC);
    }
}
