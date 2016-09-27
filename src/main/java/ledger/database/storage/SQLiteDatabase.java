package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.Transaction;

import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Database handler for SQLite storage mechanism.
 */
public class SQLiteDatabase implements IDatabase {

    private Connection database;

    public SQLiteDatabase(InputStream iStream) {
        // Initalize SQLite streams.
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        database = DriverManager.getConnection("jdbc:sqlite:src/test/resources/test.db");

        initalizeDatabase();
    }

    public void initalizeDatabase() {
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


    public void shutdown() {
        database.close();
    }

    public void insertTransaction(Transaction transaction) {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO Transaction (TRANS_DATETIME,TRANS_AMOUNT,TRANS_PENDING,TRANS_ACCOUNT_ID,TRANS_PAYEE_ID) VALUES (?, ?, ?, ?, ?)");
            stmt.setDate(1, Transaction.date);
            stmt.setInt(2, Transaction.amount);
            stmt.setBoolean(3, Transaction.pending);
            stmt.setInt(4, Transaction.account.ID);
            stmt.setInt(5, Transaction.payee.ID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    ;

    public void deleteTransaciton(Transaction transaction) {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM Transaction WHERE TRANSACTION_ID = ?");
            stmt.setInt(1, Transaction.ID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    ;

    public void editTransaction(Transaction transaction) {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE Transaction SET TRANS_DATETIME=?,TRANS_AMOUNT=?,TRANS_PENDING=?,TRANS_ACCOUNT_ID=?,TRANS_PAYEE_ID=? WHERE TRANS_ID=?");
            stmt.setDate(1, Transaction.date);
            stmt.setInt(2, Transaction.amount);
            stmt.setBoolean(3, Transaction.pending);
            stmt.setInt(4, Transaction.account.ID);
            stmt.setInt(5, Transaction.payee.ID);
            stmt.setInt(6, Transaction.ID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    ;

    public List<Transaction> getAllTransactions() {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Transaction;");

            ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

            while ( rs.next() ) {
                Date date = new Date(rs.getLong("TRANS_DATETIME"));
                int transactionID = rs.getInt("TRANS_ID");
                int amount = rs.getInt("TRANS_AMOUNT");
                boolean pending = rs.getBoolean("TRANS_PENDING");
                int accountID = rs.getInt("TRANS_ACCOUNT_ID");
                int payeeID = rs.getInt("TRANS_PAYEE_ID");

                Account account = getAccountForID(accountID);
                Payee payee = getPayeeForID(payeeID);
                List<Tag> tags = getTagsForTransactionID()
                Note note = getNoteForID(noteID);

                Transaction currentTransaction = new Transaction(date, type, amount, account, payee, pending, transactionID, tags, note);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAccount(Account account) {
        return;
    }

    ;

    public void deleteAccount(Account account) {
        return;
    }

    ;

    public void editAccount(Account account) {
        return;
    }

    ;

    public void insertPayee(Payee payee) {
        return;
    }

    ;

    public void deletePayee(Payee payee) {
        return;
    }

    ;

    public void editPayee(Payee payee) {
        return;
    }

    ;

    public void insertType(Type type) {
        return;
    }

    ;

    public void deleteType(Type type) {
        return;
    }

    ;

    public void editType(Type type) {
        return;
    }

    ;

}
