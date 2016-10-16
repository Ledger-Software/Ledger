package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
public class AccountTable {
    private static final String tableAccount = "CREATE TABLE IF NOT EXISTS ACCOUNT " +
            "(ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ACCOUNT_NAME TEXT           NOT NULL, " +
            "ACCOUNT_DESC TEXT           NOT NULL" +
            ")";

    public static final String TABLE_NAME = "ACCOUNT";

    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    public static final String ACCOUNT_DESC = "ACCOUNT_DESC";

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_DESC);
    }

}
