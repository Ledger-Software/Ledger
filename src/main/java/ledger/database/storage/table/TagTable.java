package ledger.database.storage.table;

public class TagTable {

    public static final String TABLE_NAME = "TAG";
    public static final String TAG_ID = "TAG_ID";
    public static final String TAG_NAME = "TAG_NAME";
    public static final String TAG_DESC = "TAG_DESC";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, TAG_ID, TAG_NAME, TAG_DESC);
    }

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the H2 Table corresponding to this object
     */
    public static String CreateStatementH2() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL" +
                ")", TABLE_NAME, TAG_ID, TAG_NAME, TAG_DESC);
    }
}
