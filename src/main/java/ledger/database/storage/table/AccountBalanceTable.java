package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
public class AccountBalanceTable {

    private static final String tableAccountBalance = "CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE " +
            "(ABAL_ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ABAL_DATETIME REAL         NOT NULL, " +
            "ABAL_AMOUNT INT            NOT NULL " +
            ")";

    public static String TABLE_NAME = "ACCOUNT_BALANCE";
    public static String ABAL_ACCOUNT_ID = "ABAL_ACCOUNT_ID";
    public static String ABAL_DATETIME = "ABAL_DATETIME";
    public static String ABAL_AMOUNT = "ABAL_AMOUNT";

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s REAL NOT NULL, " +
                "%s INT NOT NULL" +
                ")", TABLE_NAME, ABAL_ACCOUNT_ID, ABAL_DATETIME, ABAL_AMOUNT);
    }
}
