package ledger.database.storage;

import ledger.database.entity.Type;
import ledger.database.storage.table.*;
import ledger.exception.StorageException;

import java.sql.*;
import java.util.LinkedList;

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

        if(this.getAllTypes().size() == 0)
            for(Type type: TypeTable.defaultTypes()) {
                this.insertType(type);
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
