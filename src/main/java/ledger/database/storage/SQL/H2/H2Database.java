package ledger.database.storage.SQL.H2;

import ledger.database.entity.Type;
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

    public H2Database(String pathToDb, String password) throws StorageException {
        // Initialize H2 streams.

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new StorageException("Unable to find h2 Driver", e);
        }

        try {
            String url = "jdbc:h2:" + pathToDb + ";CIPHER=AES";
            String user = "TransACT";
            String pwds = "Ledger " + password;
            databaseObject = DriverManager.getConnection(url, user, pwds);
        } catch (SQLException e) {
            throw new StorageException("Unable to connect to JDBC Socket. Either the file or password is invalid.", e);
        }
        initializeDatabase();
    }

    // DB management functions
    @Override
    public void initializeDatabase() throws StorageException {
        LinkedList<String> tableSQL = new LinkedList<>();

        tableSQL.add(TagTable.CreateStatementH2());
        tableSQL.add(TypeTable.CreateStatementH2());
        tableSQL.add(AccountTable.CreateStatementH2());
        tableSQL.add(AccountBalanceTable.CreateStatementH2());
        tableSQL.add(PayeeTable.CreateStatementH2());

        tableSQL.add(TransactionTable.CreateStatementH2());
        tableSQL.add(NoteTable.CreateStatementH2());

        tableSQL.add(TagToTransTable.CreateStatementH2());
        tableSQL.add(TagToPayeeTable.CreateStatementH2());

        try {
            for (String statement : tableSQL) {
                Statement stmt = getDatabase().createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }

        if (this.getAllTypes().size() == 0)
            for (Type type : TypeTable.defaultTypes()) {
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
