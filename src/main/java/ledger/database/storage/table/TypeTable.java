package ledger.database.storage.table;

import ledger.database.entity.Type;
import ledger.io.input.TypeConversion;

import java.util.List;

public class TypeTable {

    public static String TABLE_NAME = "TYPE";
    public static String TYPE_ID = "TYPE_ID";
    public static String TYPE_NAME = "TYPE_NAME";
    public static String TYPE_DESC = "TYPE_DESC";

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
                ")", TABLE_NAME, TYPE_ID, TYPE_NAME, TYPE_DESC);
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
                ")", TABLE_NAME, TYPE_ID, TYPE_NAME, TYPE_DESC);
    }


    public static List<Type> defaultTypes() {
        return TypeConversion.getAllTypes();
    }
}
