package ledger.database.storage.table;

/**
 * Created by CJ on 10/16/2016.
 */
public class TagTable {

    static final String tableTag = "CREATE TABLE IF NOT EXISTS TAG " +
            "(TAG_ID INTEGER PRIMARY KEY    AUTOINCREMENT, " +
            "TAG_NAME TEXT              NOT NULL, " +
            "TAG_DESC TEXT              NOT NULL" +
            ")";

    public static String TABLE_NAME = "TAG";
    public static String TAG_ID = "TAG_ID";
    public static String TAG_NAME = "TAG_NAME";
    public static String TAG_DESC = "TAG_DESC";

    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, TAG_ID, TAG_NAME, TAG_DESC);
    }
}
