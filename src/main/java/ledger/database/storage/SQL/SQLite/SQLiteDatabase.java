package ledger.database.storage.SQL.SQLite;

import ledger.database.entity.Frequency;
import ledger.database.entity.Type;
import ledger.database.storage.SQL.*;
import ledger.database.storage.table.*;
import ledger.exception.StorageException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Database handler for SQLite storage mechanism.
 */
@SuppressWarnings("SqlDialectInspection") // TODO: Find how to get this integration working.
public class SQLiteDatabase extends DataBaseManager implements ISQLDatabaseAccountBalance, ISQLDatabaseTransaction, ISQLDatabaseNote, ISQLDatabasePayee, ISQLDatabaseAccount, ISQLDatabaseTag, ISQLDatabaseType, ISQLDatabaseIgnoredExpression, ISQLDatabaseRecurringTransaction, ISQLFrequency {

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

        tableSQL.add(TagTable.CreateStatementSQLite());
        tableSQL.add(TypeTable.CreateStatementSQLite());
        tableSQL.add(AccountTable.CreateStatementSQLite());
        tableSQL.add(AccountBalanceTable.CreateStatementSQLite());
        tableSQL.add(PayeeTable.CreateStatementSQLite());

        tableSQL.add(TransactionTable.CreateStatementSQLite());
        tableSQL.add(NoteTable.CreateStatementSQLite());

        tableSQL.add(TagToTransTable.CreateStatementSQLite());
        tableSQL.add(TagToPayeeTable.CreateStatementSQLite());

        tableSQL.add((IgnoredExpressionTable.CreateStatementSQLite()));

        tableSQL.add(RecurringTransactionTable.CreateStatementSQLite());
        tableSQL.add(FrequencyTable.CreateStatementSQLite());

        executeList(tableSQL);

        if (this.getAllTypes().size() == 0) {
            for (Type type : TypeTable.defaultTypes()) {
                this.insertType(type);
            }
        }

        if (this.getAllFrequencies().size() == 0) {
            for (Frequency freq : FrequencyTable.getDefaultFrequencies()) {
                this.insertFrequency(freq);
            }
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
