package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;

import ledger.exception.StorageException;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Database handler for SQLite storage mechanism.
 */
public class SQLiteDatabase implements IDatabase {

    private Connection database;

    public SQLiteDatabase(InputStream iStream) throws StorageException {
        // Initialize SQLite streams.

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        try {
            database = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch (SQLException e) {
            throw new StorageException("Unable to connect to JDBC Socket. ");
        }

        initializeDatabase();
    }

    public void initializeDatabase() throws StorageException {
        LinkedList<String> tableSQL = new LinkedList<>();

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
            for (String statement : tableSQL) {
                Statement stmt = database.createStatement();
                stmt.execute(statement);
                stmt.close();
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }
    }

    private final String tableTransaction = "CREATE TABLE IF NOT EXISTS TRANSACT " +
            "(TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "TRANS_DATETIME REAL        NOT NULL, " +
            "TRANS_AMOUNT INT           NOT NULL," +
            "TRANS_PENDING BOOLEAN      NOT NULL, " +
            "TRANS_ACCOUNT_ID INT       NOT NULL, " +
            "TRANS_PAYEE_ID INT         NOT NULL, " +
            "TRANS_TYPE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TRANS_ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID), " +
            "FOREIGN KEY(TRANS_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)," +
            "FOREIGN KEY(TRANS_TYPE_ID) REFERENCES TYPE(TYPE_ID)" +
            ")";
    private final String tableNote = "CREATE TABLE IF NOT EXISTS NOTE" +
            "(NOTE_TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "NOTE_TEXT TEXT             NOT NULL, " +
            "FOREIGN KEY(NOTE_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";

    private final String tableTagToTrans= "CREATE TABLE IF NOT EXISTS TAG_TO_TRANS " +
            "(TTTS_TAG_ID INT           NOT NULL, " +
            "TTTS_TRANS_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTTS_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTTS_TRANS_ID) REFERENCES TRANSACT(TRANS_ID)" +
            ")";
    private final String tableTag = "CREATE TABLE IF NOT EXISTS TAG " +
            "(TAG_ID INT PRIMARY KEY    NOT NULL, " +
            "TAG_NAME TEXT              NOT NULL, " +
            "TAG_DESC TEXT              NOT NULL" +
            ")";
    private final String tableType = "CREATE TABLE IF NOT EXISTS TYPE " +
            "(TYPE_ID INT PRIMARY KEY   NOT NULL, " +
            "TYPE_NAME TEXT              NOT NULL, " +
            "TYPE_DESC TEXT              NOT NULL" +
            ")";
    private final String tableAccount = "CREATE TABLE IF NOT EXISTS ACCOUNT " +
            "(ACCOUNT_ID INT PRIMARY KEY NOT NULL, " +
            "ACCOUNT_NAME TEXT           NOT NULL, " +
            "ACCOUNT_DESC TEXT           NOT NULL" +
            ")";
    private final String tableAccountBalance = "CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE " +
            "(ABAL_ACCOUNT_ID INT       NOT NULL, " +
            "ABAL_DATETIME REAL         NOT NULL, " +
            "ABAL_AMOUNT INT            NOT NULL " +
            ")";
    private final String tablePayee = "CREATE TABLE IF NOT EXISTS PAYEE " +
            "(PAYEE_ID INT PRIMARY KEY  NOT NULL, " +
            "PAYEE_NAME TEXT           NOT NULL, " +
            "PAYEE_DESC TEXT           NOT NULL" +
            ")";
    private final String tableTagToPayee = "CREATE TABLE IF NOT EXISTS TAG_TO_PAYEE " +
            "(TTPE_TAG_ID INT           NOT NULL, " +
            "TTPE_PAYEE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";


    public void shutdown() throws StorageException {
        try {
            database.close();
        } catch (SQLException e) {
            throw new StorageException("Exception while shutting down database.", e);
        }
    }

    // TODO: Review logic and implement helper methods
    public void insertTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TRANSACTION (TRANS_DATETIME,TRANS_AMOUNT,TRANS_TYPE_ID,TRANS_PENDING,TRANS_ACCOUNT_ID,TRANS_PAYEE_ID) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                System.out.println("ERROR: No matching transaction type found.");
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForName(transaction.getAccount().getName());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                Account insertedAccount = getAccountForName(transaction.getAccount().getName());
                stmt.setInt(5, insertedAccount.getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                Payee insertedPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
                stmt.setInt(6, insertedPayee.getId());
            }

            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTransactionID = generatedIDs.getInt("TRANS_ID");

                for (Tag currentTag : transaction.getTagList()) {
                    Tag existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                    if (existingTag != null) {
                        insertTagToTrans(existingTag.getId(), insertedTransactionID);
                    } else {
                        insertTag(currentTag);
                        Tag insertedTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                        insertTagToTrans(insertedTag.getId(), insertedTransactionID);
                    }
                }
            }

            /* Transaction Notes are not added on transaction insertion. By principle, notes should always be added
               after the fact, by the user.
             */

            stmt.close();
            generatedIDs.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding transaction", e);
        }
    }

    private Type getTypeForName(String name) {
        return null;
    }

    public void deleteTransaction(Transaction transaction) throws StorageException{
        try {
            PreparedStatement deleteTransactionStmt = database.prepareStatement("DELETE FROM TRANSACTION WHERE TRANS_ID = ?");
            deleteTransactionStmt.setInt(1, transaction.getId());
            deleteTransactionStmt.executeUpdate();
            deleteTransactionStmt.close();

            PreparedStatement deleteNoteStmt = database.prepareStatement("DELETE FROM NOTE WHERE NOTE_TRANS_ID = ?");
            deleteNoteStmt.setInt(1, transaction.getId());
            deleteNoteStmt.executeUpdate();
            deleteNoteStmt.close();

            PreparedStatement deleteTagToTransactionStmt = database.prepareStatement("DELETE FROM TAG_TO_TRANS WHERE TTTS_TRANS_ID = ?");
            deleteTagToTransactionStmt.setInt(1, transaction.getId());
            deleteTagToTransactionStmt.executeUpdate();
            deleteTagToTransactionStmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting transaction", e);
        }
    }

    // TODO: Review logic and implement helper methods
    public void editTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TRANSACTION SET TRANS_DATETIME=?,TRANS_AMOUNT=?,TRANS_TYPE_ID=?,TRANS_PENDING=?,TRANS_ACCOUNT_ID=?,TRANS_PAYEE_ID=? WHERE TRANS_ID=?");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                System.out.println("ERROR: No matching transaction type found.");
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForName(transaction.getAccount().getName());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                Account insertedAccount = getAccountForName(transaction.getAccount().getName());
                stmt.setInt(5, insertedAccount.getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                Payee insertedPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
                stmt.setInt(6, insertedPayee.getId());

            }

            stmt.setInt(7, transaction.getId());

            stmt.executeUpdate();

            // Update tag associations
            deleteAllTagToTransForTransactionID(transaction.getId());
            for (Tag currentTag : transaction.getTagList()) {
                Tag existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                if (existingTag != null) {
                    insertTagToTrans(existingTag.getId(), transaction.getId());
                } else {
                    insertTag(currentTag);
                    Tag insertedTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                    insertTagToTrans(insertedTag.getId(), transaction.getId());

                }
            }

            // Update transaction note
            Note updatedNote = transaction.getNote();
            if (updatedNote != null) {
                Note existingNote = getNoteForTransactionID(transaction.getId());
                existingNote.setNoteText(updatedNote.getNoteText());
                editNote(existingNote);
            } else {
                deleteNoteForTransactionID(transaction.getId());
            }

            // Close SQL statement
            stmt.close();

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing transaction", e);
        }
    }

    private void deleteNoteForTransactionID(int id) {
        // TODO: Need Impl.
    }

    private void deleteAllTagToTransForTransactionID(int id) {
        // TODO: Need Impl.
    }

    // TODO: Review logic and implement helper methods
    public List<Transaction> getAllTransactions() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Transaction;");

            ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

            while (rs.next()) {
                Date date = new Date(rs.getLong("TRANS_DATETIME"));
                int transactionID = rs.getInt("TRANS_ID");
                int typeID = rs.getInt("TRANS_TYPE_ID");
                int amount = rs.getInt("TRANS_AMOUNT");
                boolean pending = rs.getBoolean("TRANS_PENDING");
                int accountID = rs.getInt("TRANS_ACCOUNT_ID");
                int payeeID = rs.getInt("TRANS_PAYEE_ID");

                Type type = getTypeForID(typeID);
                Account account = getAccountForID(accountID);
                Payee payee = getPayeeForID(payeeID);
                List<Tag> tags = getTagsForTransactionID(transactionID);
                Note note = getNoteForTransactionID(transactionID);

                Transaction currentTransaction = new Transaction(date, type, amount, account, payee, pending, transactionID, tags, note);

                transactionList.add(currentTransaction);
            }

            // Close SQLite statements and result sets
            stmt.close();
            rs.close();


            return transactionList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all transactions", e);
        }
    }

    private Note getNoteForTransactionID(int transactionID) {
        return null;
    }

    private List<Tag> getTagsForTransactionID(int transactionID) {
        return null;
    }

    private Payee getPayeeForID(int payeeID) {
        return null;
    }

    private Account getAccountForID(int accountID) {
        return null;
    }

    private Type getTypeForID(int typeID) {
        return null;
    }

    public void insertAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO ACCOUNT (ACCOUNT_NAME,ACCOUNT_DESC) VALUES (?, ?)");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Account", e);
        }
    }


    public void deleteAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?");
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Account", e);
        }
    }

    public void editAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE ACCOUNT SET ACCOUNT_NAME=?, ACCOUNT_DESC=? WHERE ACCOUNT_ID=?");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.setInt(3, account.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Account", e);
        }
    }

    public void insertPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("INSERT INTO PAYEE (PAYEE_NAME, PAYEE_DESC) VALUES (?, ?, ?)");
            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }


    public void deletePayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM PAYEE WHERE PAYEE_ID = ?");
            stmt.setInt(1, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Payee", e);
        }
    }


    public void editPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("UPDATE PAYEE SET PAYEE_NAME = ?, PAYEE_DESC = ? WHERE PAYEE_ID = ?");

            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.setInt(3, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Payee", e);
        }
    }
    public void insertNote(Note note) {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("INSERT INTO NOTE (NOTE_TEXT, NOTE_TRANS_ID) VALUES (?, ?)");
            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteNote(Note note) {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM NOTE WHERE NOTE_TRANS_ID = ?");
            stmt.setInt(1, note.getTransactionId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public void editNote(Note note) {

        try {
            PreparedStatement stmt =
                    database.prepareStatement("UPDATE NOTE SET NOTE_TEXT=? WHERE NOTE_TRANS_ID = ?");

            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TYPE (TYPE_NAME,TYPE_DESC) VALUES (?, ?)");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Type", e);
        }
    }


    public void deleteType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TYPE WHERE TYPE_ID = ?");
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Type", e);
        }
    }

    public void editType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TYPE SET TYPE_NAME=?, TYPE_DESC=? WHERE TRANS_ID=?");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing type");
        }
    }

    private Account getAccountForName(String name) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_NAME=?");
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();
            int count = 0;

            String newName = "";
            String description = "";
            int id = -1;

            while (rs.next()) {
                newName = rs.getString("ACCOUNT_NAME");
                description = rs.getString("ACCOUNT_DESC");
                id = rs.getInt("ACCOUNT_ID");
                count++;
            }

            rs.close();
            stmt.close();

            if (count == 0) return null;

            return new Account(newName, description, id);

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }

    public void insertTag(Tag tag) {
        try {
        PreparedStatement stmt = database.prepareStatement("INSERT INTO TAG (TAG_NAME,AT/) VALUES (?, ?)");
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getDescription());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
    public Tag getTagForNameAndDescription(String tagName, String tagDescription){
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM TAG WHERE TAG_NAME=? AND TAG_DESC=?");
            stmt.setString(1, tagName);
            stmt.setString(2, tagDescription);
            ResultSet rs =stmt.executeQuery();
            int count = 0;
            String rstagName = "";
            String rstagDesc = "";
            int rsid = -1;
            while (rs.next()) {
                rstagName = rs.getString("TAG_NAME");
                rstagDesc = rs.getString("TAG_DESC");
                rsid = rs.getInt("TAG_ID");
                count++;
            }
            rs.close();
            stmt.close();
            if(count ==0){
                return  null;
            }


            return new Tag(rstagName, rstagDesc, rsid);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTag(Tag tag) {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TAG WHERE TAG_ID = ?");
            stmt.setInt(1, tag.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public void editTag(Tag tag) {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TAG SET TAG_NAME=?, TAG_DESC=? WHERE TAG_ID=?");
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getDescription());
            stmt.setInt(3, tag.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription) {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM PAYEE WHERE PAYEE_NAME=? AND PAYEE_DESC = ?");
            stmt.setString(1, payeeName);
            stmt.setString(2, payeeDescription);

            ResultSet rs = stmt.executeQuery();
            int count = 0;

            String newName = "";
            String description = "";
            int id = -1;

            while (rs.next()) {
                newName = rs.getString("PAYEE_NAME");
                description = rs.getString("PAYEE_DESC");
                id = rs.getInt("PAYEE_ID");
            }

            rs.close();
            stmt.close();

            if (count == 0) return null;

            return new Payee(newName, description, id);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertTagToTrans(int tagID, int transID) {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TAG_TO_TRANS (TTTS_TAG_ID,TTTS_TRANS_ID) VALUES (?, ?)");
            stmt.setInt(1, tagID);
            stmt.setInt(2, transID);
            stmt.executeUpdate();
            stmt.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
