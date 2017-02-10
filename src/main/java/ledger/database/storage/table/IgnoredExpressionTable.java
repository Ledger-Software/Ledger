package ledger.database.storage.table;

public class IgnoredExpressionTable {

    public static final String TABLE_NAME = "IGNORE_EXPRESSIONS";
    public static final String IGNORE_ID = "IGNORE_ID";
    public static final String IGNORE_EXPRESSION = "IGNORE_STRING";
    public static final String MATCH_OR_CONTAIN = "IS_MATCH";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "%s BOOL NOT NULL" +
                ")", TABLE_NAME, IGNORE_ID, IGNORE_EXPRESSION, MATCH_OR_CONTAIN);
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
                "%s BOOL NOT NULL" +
                ")", TABLE_NAME, IGNORE_ID, IGNORE_EXPRESSION, MATCH_OR_CONTAIN);
    }
}
