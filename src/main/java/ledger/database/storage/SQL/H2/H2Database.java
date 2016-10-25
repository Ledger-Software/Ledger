package ledger.database.storage.SQL.H2;

import ledger.database.storage.SQL.*;
import ledger.database.storage.table.*;
import ledger.exception.StorageException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

/**
 * Database handler for h2 storage mechanism.
 */
@SuppressWarnings("SqlDialectInspection") // TODO: Find how to get this integration working.
public class H2Database implements ISQLDatabaseTransaction, ISQLDatabaseNote, ISQLDatabasePayee, ISQLDatabaseAccount, ISQLDatabaseTag, ISQLDatabaseType {

    private Connection databaseObject;

    public H2Database(String pathToDb) throws StorageException {
        // Initialize SQLite streams.

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        try {
            databaseObject = DriverManager.getConnection("jdbc:h2:" + pathToDb);
        } catch (SQLException e) {
            throw new StorageException("Unable to connect to JDBC Socket. ");
        }
        initializeDatabase();
    }

    // DB management functions
    @Override
    public void initializeDatabase() throws StorageException {
        LinkedList<String> tableSQL = new LinkedList<>();

        tableSQL.add(TagTable.CreateStatement());
        tableSQL.add(TypeTable.CreateStatement());
        tableSQL.add(AccountTable.CreateStatement());
        tableSQL.add(AccountBalanceTable.CreateStatement());
        tableSQL.add(PayeeTable.CreateStatement());

        tableSQL.add(TransactionTable.CreateStatement());
        tableSQL.add(NoteTable.CreateStatement());

        tableSQL.add(TagToTransTable.CreateStatement());
        tableSQL.add(TagToPayeeTable.CreateStatement());

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
