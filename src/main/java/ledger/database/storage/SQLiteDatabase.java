package ledger.database.storage;

import ledger.database.IDatabase;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
        Statement stmt = database.createStatement();
        String createTableSQL = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " ADDRESS        CHAR(50), " +
                " SALARY         REAL)";
        stmt.execute(createTableSQL);
        stmt.close();
    }

    private boolean doesTableExist(String tableName) {
        Statement stmt = database.createStatement();
        String createTableSQL = "CREATE TABLE COMPANY " +
                "(ID INT PRIMARY KEY     NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " ADDRESS        CHAR(50), " +
                " SALARY         REAL)";
        stmt.execute(createTableSQL);
        stmt.close();
    }

    private final String tableTransaction = "";
    private final String tableTransaction = "";
    private final String tableTransaction = "";
    private final String tableTransaction = "";
    private final String tableTransaction = "";
    private final String tableTransaction = "";
    private final String tableTransaction = "";


    private void shutdown() {
        database.close();
    }

}
