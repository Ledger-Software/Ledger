package ledger.database.storage.table;

public class NoteTable {

    public static final String TABLE_NAME = "NOTE";
    public static final String NOTE_TRANS_ID = "NOTE_TRANS_ID";
    public static final String NOTE_TEXT = "NOTE_TEXT";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatementSQLite() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                ")", TABLE_NAME, NOTE_TRANS_ID, NOTE_TEXT, NOTE_TRANS_ID, TransactionTable.TABLE_NAME, TransactionTable.TRANS_ID);
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
                "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE" +
                ")", TABLE_NAME, NOTE_TRANS_ID, NOTE_TEXT, NOTE_TRANS_ID, TransactionTable.TABLE_NAME, TransactionTable.TRANS_ID);
    }
}
