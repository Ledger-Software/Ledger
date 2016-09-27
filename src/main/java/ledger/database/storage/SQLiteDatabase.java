package ledger.database.storage;

import ledger.database.IDatabase;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Database handler for SQLite storage mechanism.
 */
public class SQLiteDatabase implements IDatabase {

    private Connection database;

    public SQLiteDatabase(InputStream iStream) {
        // Initalize SQLite streams.
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver",e);
        }

        database = DriverManager.getConnection("jdbc:sqlite:src/test/resources/test.db");

        initalize();
    }

    private void initalizeDatabase() {
        LinkedList<String> tableSQL = new LinkedList<String>();

        tableSQL.add(tableTag);
        tableSQL.add(tableType);
        tableSQL.add(tableAccount);
        tableSQL.add(tableAccountBalance);
        tableSQL.add(tablePayee);

        tableSQL.add(tableTransaction);
        tableSQL.add(tableNote);

        tableSQL.add(tableTagToTrans);
        tableSQL.add(tableTagToPayee);

        for(String statement: tableSQL) {
            Statement stmt = database.createStatement();
            stmt.execute(statement);
            stmt.close();
        }
    }

    private boolean doesTableExist(String tableName) {
        Statement stmt = database.createStatement();
        String createTableSQL = "SELECT name FROM sqlite_master WHERE type='table' AND name='{1}'";
        ResultSet result = stmt.executeQuery(String.format(createTableSQL, tableName));
        stmt.close();

        while(result.next()) {
            return true;
        }
        return false;
    }

    private final String tableTransaction = "CREATE TABLE TRANSACTION " +
            "(TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "TRANS_DATETIME REAL        NOT NULL, " +
            "TRANS_AMOUNT INT           NOT NULL," +
            "TRANS_PENDING BOOLEAN      NOT NULL, " +
            "TRANS_ACCOUNT_ID INT       NOT NULL, " +
            "TRANS_PAYEE_ID             NOT NULL, " +
            "FOREIGN KEY(TRANS_ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID), " +
            "FOREIGN KEY(TRANS_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";
    private final String tableNote = "CREATE TABLE NOTE" +
            "(NOTE_TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "NOTE_TEXT TEXT             NOT NULL, " +
            "FOREIGN KEY(NOTE_TRANS_ID) REFERENCES TRANSACTION(TRANS_ID)" +
            ")";
    private final String tableTagToTrans= "CREATE TABLE IF NOT EXISTS TAG_TO_TRANS " +
            "(TTTS_TAG_ID INT           NOT NULL," +
            "TTTS_TRANS_ID" +
            "FOREIGN KEY(TTTS_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTTS_TRANS_ID) REFERENCES TRANSACTION(TRANS_ID)" +
            ")";
    private final String tableTag = "CREATE TABLE IF NOT EXISTS TAG " +
            "(TAG_ID INT PRIMARY KEY    NOT NULL, " +
            "TAG_NAME TEXT              NOT NULL, " +
            "TAG_DESC TEXT              NOT NULL" +
            ")";
    private final String tableType= "CREATE TABLE IF NOT EXISTS TYPE " +
            "(TYPE_ID INT PRIMARY KEY   NOT NULL, " +
            "TYPE_NAME TEXT              NOT NULL, " +
            "TYPE_DESC TEXT              NOT NULL" +
            ")";
    private final String tableAccount= "CREATE TABLE IF NOT EXISTS ACCOUNT, " +
            "(ACCOUNT_ID INT PRIMARY KEY NOT NULL, " +
            "ACCOUNT_NAME TEXT           NOT NULL, " +
            "ACCOUNT_DESC TEXT           NOT NULL" +
            ")";
    private final String tableAccountBalance= "CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE " +
            "(ABAL_ACCOUNT_ID INT       NOT NULL, " +
            "ABAL_DATETIME REAL         NOT NULL, " +
            "ABAL_AMOUNT INT            NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";
    private final String tablePayee= "CREATE TABLE IF NOT EXISTS PAYEE " +
            "(PAYEE_ID INT PRIMARY KEY  NOT NULL, " +
            "PAYEE_NAME TEXT           NOT NULL, " +
            "PAYEE_DESC TEXT           NOT NULL" +
            ")";
    private final String tableTagToPayee= "CREATE TABLE IF NOT EXISTS TAG_TO_PAYEE " +
            "(TTPE_TAG_ID INT           NOT NULL, " +
            "TTPE_PAYEE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";



    private void shutdown() {
        database.close();
    }

}
