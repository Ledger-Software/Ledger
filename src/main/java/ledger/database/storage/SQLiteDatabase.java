package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.Transaction;
import org.omg.SendingContext.RunTime;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

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

        try {
            database = DriverManager.getConnection("jdbc:sqlite:src/test/resources/test.db");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to JDBC Socket. ")
        }

        initalizeDatabase();
    }

    public void initalizeDatabase() {
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

        try {
            for(String statement: tableSQL) {
                Statement stmt = database.createStatement();
                stmt.execute(statement);
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to Create Table", e);
        }
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



    public void shutdown() {
        try {
            database.close();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while shutting down database.", e);
        }
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

    public void deleteTransaction(Transaction transaction) {
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
                List<Tag> tags = getTagsForTransactionID();
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
