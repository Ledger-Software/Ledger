package ledger.database.storage.table;

public class NoteTable {

    private static final String tableNote = "CREATE TABLE IF NOT EXISTS NOTE" +
            "(NOTE_TRANS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "NOTE_TEXT TEXT             NOT NULL, " +
            "FOREIGN KEY(NOTE_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";

    public static final String TABLE_NAME = "NOTE";
    public static final String NOTE_TRANS_ID = "NOTE_TRANS_ID";
    public static final String NOTE_TEXT = "NOTE_TEXT";

    /**
     * Creates the String command to create the table for this object.
     *
     * @return String for creating the SQLite Table corresponding to this object
     */
    public static String CreateStatement() {
        return String.format("CREATE TABLE IF NOT EXISTS %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT NOT NULL, " +
                "FOREIGN KEY(%s) REFERENCES %s(%s)" +
                ")", TABLE_NAME, NOTE_TRANS_ID, NOTE_TEXT, NOTE_TRANS_ID, TransactionTable.TABLE_NAME, TransactionTable.TRANS_ID);
    }
}
