package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.exception.StorageException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Database handler for SQLite storage mechanism.
 */
@SuppressWarnings("SqlDialectInspection") // TODO: Find how to get this integration working.
public class SQLiteDatabase implements ISQLiteDatabaseTransaction, ISQLiteDatabaseNote, ISQLiteDatabasePayee, ISQLiteDatabaseAccount, ISQLiteDatabaseTag, ISQLiteDatabaseType {

    private Connection databaseObject;

    public SQLiteDatabase(String pathToDb) throws StorageException {
        // Initialize SQLite streams.

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        try {
            databaseObject = DriverManager.getConnection("jdbc:sqlite:" + pathToDb);
        } catch (SQLException e) {
            throw new StorageException("Unable to connect to JDBC Socket. ");
        }
        initializeDatabase();
    }

    // DB management functions
    @Override
    public void initializeDatabase() throws StorageException {
        LinkedList<String> tableSQL = new LinkedList<>();

        tableSQL.add(SQLiteTableConstants.tableTag);
        tableSQL.add(SQLiteTableConstants.tableType);
        tableSQL.add(SQLiteTableConstants.tableAccount);
        tableSQL.add(SQLiteTableConstants.tableAccountBalance);
        tableSQL.add(SQLiteTableConstants.tablePayee);

        tableSQL.add(SQLiteTableConstants.tableTransaction);
        tableSQL.add(SQLiteTableConstants.tableNote);

        tableSQL.add(SQLiteTableConstants.tableTagToTrans);
        tableSQL.add(SQLiteTableConstants.tableTagToPayee);

        try {
            for (String statement : tableSQL) {
                Statement stmt = getDatabase().createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }
    }

    @Override
    public void shutdown() throws StorageException {
        try {
            getDatabase().close();
        } catch (SQLException e) {
            throw new StorageException("Exception while shutting down database.", e);
        }
    }

    @Override
    public void rollbackDatabase() throws StorageException {
        try {
            getDatabase().rollback();
        } catch (SQLException e) {
            throw new StorageException("Error while performing database rollback", e);
        }
    }

    @Override
    public void setDatabaseAutoCommit(boolean autoCommit) throws StorageException {
        try {
            getDatabase().setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new StorageException("Error while setting database autocommit to " + autoCommit, e);
        }
    }

    public Connection getDatabase() {
        return databaseObject;
    }
}
