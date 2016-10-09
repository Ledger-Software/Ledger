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
public class SQLiteDatabase implements IDatabase {

    private Connection database;

    public SQLiteDatabase(String pathToDb) throws StorageException {
        // Initialize SQLite streams.

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        try {
            database = DriverManager.getConnection("jdbc:sqlite:" + pathToDb);
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
                Statement stmt = database.createStatement();
                stmt.execute(statement);
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }
    }

    @Override
    public void shutdown() throws StorageException {
        try {
            database.close();
        } catch (SQLException e) {
            throw new StorageException("Exception while shutting down database.", e);
        }
    }

    // Basic CRUD functionality
    @Override
    public void insertTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TRANSACT (TRANS_DATETIME,TRANS_AMOUNT,TRANS_TYPE_ID,TRANS_PENDING,TRANS_ACCOUNT_ID,TRANS_PAYEE_ID) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                insertType(transaction.getType());
                stmt.setInt(3, transaction.getType().getId());
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForName(transaction.getAccount().getName());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                stmt.setInt(5, transaction.getAccount().getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                stmt.setInt(6, transaction.getPayee().getId());
            }

            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTransactionID = generatedIDs.getInt(1);

                for (Tag currentTag : transaction.getTagList()) {
                    Tag existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                    if (existingTag != null) {
                        insertTagToTrans(existingTag.getId(), insertedTransactionID);
                    } else {
                        insertTag(currentTag);
                        insertTagToTrans(currentTag.getId(), insertedTransactionID);
                    }
                }
                transaction.setId(insertedTransactionID);
            }

            /* Transaction Notes are not added on transaction insertion. By principle, notes should always be added
               after the fact, by the user.
             */

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding transaction", e);
        }
    }

    @Override
    public void deleteTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement deleteTransactionStmt = database.prepareStatement("DELETE FROM TRANSACT WHERE TRANS_ID = ?");
            deleteTransactionStmt.setInt(1, transaction.getId());
            deleteTransactionStmt.executeUpdate();

            deleteNoteForTransactionID(transaction.getId());
            deleteAllTagToTransForTransactionID(transaction.getId());

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting transaction", e);
        }
    }

    @Override
    public void editTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TRANSACT SET TRANS_DATETIME=?,TRANS_AMOUNT=?,TRANS_TYPE_ID=?,TRANS_PENDING=?,TRANS_ACCOUNT_ID=?,TRANS_PAYEE_ID=? WHERE TRANS_ID=?");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                // TODO: Discuss
                System.out.println("ERROR: No matching transaction type found.");
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForName(transaction.getAccount().getName());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                stmt.setInt(5, transaction.getAccount().getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                stmt.setInt(6, transaction.getPayee().getId());

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
                    insertTagToTrans(currentTag.getId(), transaction.getId());

                }
            }

            // Update transaction note
            Note updatedNote = transaction.getNote();
            if (updatedNote != null) {
                Note existingNote = getNoteForTransactionID(transaction.getId());
                if (existingNote != null) {
                    existingNote.setNoteText(updatedNote.getNoteText());
                    editNote(existingNote);
                }
            } else {
                deleteNoteForTransactionID(transaction.getId());
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing transaction", e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TRANSACT;");

            ArrayList<Transaction> transactionList = new ArrayList<>();

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

                Transaction currentTransaction = new Transaction(date, type, amount, account, payee, pending, tags, note, transactionID);

                transactionList.add(currentTransaction);
            }

            return transactionList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all transactions", e);
        }
    }

    @Override
    public void insertAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO ACCOUNT (ACCOUNT_NAME,ACCOUNT_DESC) VALUES (?, ?)");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedAccountID = generatedIDs.getInt(1);
                account.setId(insertedAccountID);
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Account", e);
        }
    }

    @Override
    public void deleteAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?");
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Account", e);
        }
    }

    @Override
    public void editAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE ACCOUNT SET ACCOUNT_NAME=?, ACCOUNT_DESC=? WHERE ACCOUNT_ID=?");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.setInt(3, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Account", e);
        }
    }

    @Override
    public List<Account> getAllAccounts() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT;");

            List<Account> accountList = new ArrayList<>();

            while (rs.next()) {

                int accountID = rs.getInt("ACCOUNT_ID");
                String accountName = rs.getString("ACCOUNT_NAME");
                String accountDesc = rs.getString("ACCOUNT_DESC");

                Account currentAccount = new Account(accountName, accountDesc, accountID);
                accountList.add(currentAccount);
            }

            return accountList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

    @Override
    public void insertPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("INSERT INTO PAYEE (PAYEE_NAME, PAYEE_DESC) VALUES (?, ?)");
            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedPayeeID = generatedIDs.getInt(1);
                payee.setId(insertedPayeeID);
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }

    @Override
    public void deletePayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM PAYEE WHERE PAYEE_ID = ?");
            stmt.setInt(1, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Payee", e);
        }
    }

    @Override
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

    @Override
    public void insertNote(Note note) {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("INSERT INTO NOTE (NOTE_TEXT, NOTE_TRANS_ID) VALUES (?, ?)");
            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteNote(Note note) {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM NOTE WHERE NOTE_TRANS_ID = ?");
            stmt.setInt(1, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editNote(Note note) {

        try {
            PreparedStatement stmt =
                    database.prepareStatement("UPDATE NOTE SET NOTE_TEXT=? WHERE NOTE_TRANS_ID = ?");

            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Note> getAllNotes() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM NOTE;");

            ArrayList<Note> notes = new ArrayList<>();

            while (rs.next()) {

                int note_trans_id = rs.getInt("NOTE_TRANS_ID");
                String note_text = rs.getString("NOTE_TEXT");

                Note currentNote = new Note(note_trans_id, note_text);

                notes.add(currentNote);
            }

            return notes;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all notes", e);
        }
    }

    @Override
    public void insertType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TYPE (TYPE_NAME,TYPE_DESC) VALUES (?, ?)");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTypeID = generatedIDs.getInt(1);
                type.setId(insertedTypeID);
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Type", e);
        }
    }

    @Override
    public void deleteType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TYPE WHERE TYPE_ID = ?");
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Type", e);
        }
    }

    @Override
    public void editType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TYPE SET TYPE_NAME=?, TYPE_DESC=? WHERE TYPE_ID=?");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing type");
        }
    }

    @Override
    public void insertTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TAG (TAG_NAME, TAG_DESC) VALUES (?, ?)");
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTagID = generatedIDs.getInt(1);
                tag.setId(insertedTagID);
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while inserting Tag", e);
        }
    }

    @Override
    public void deleteTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TAG WHERE TAG_ID = ?");
            stmt.setInt(1, tag.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Tag", e);
        }
    }

    @Override
    public void editTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TAG SET TAG_NAME=?, TAG_DESC=? WHERE TAG_ID=?");
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getDescription());
            stmt.setInt(3, tag.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Tag", e);
        }
    }

    @Override
    public List<Tag> getAllTags() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TAG;");

            ArrayList<Tag> tags = new ArrayList<>();

            while (rs.next()) {

                int tagID = rs.getInt("TAG_ID");
                String tagName = rs.getString("TAG_NAME");
                String tagDescription = rs.getString("TAG_DESC");

                Tag currentTag = new Tag(tagName, tagDescription, tagID);

                tags.add(currentTag);
            }
            return tags;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all notes", e);
        }
    }

    @Override
    public List<Payee> getAllPayees() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PAYEE;");

            ArrayList<Payee> payeeList = new ArrayList<>();

            while (rs.next()) {

                int payeeID = rs.getInt("PAYEE_ID");
                String payeeName = rs.getString("PAYEE_NAME");
                String payeeDesc = rs.getString("PAYEE_DESC");

                Payee currentPayee = new Payee(payeeName, payeeDesc, payeeID);

                payeeList.add(currentPayee);
            }
            return payeeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

    @Override
    public List<Type> getAllTypes() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TYPE;");

            List<Type> typeList = new ArrayList<>();

            while (rs.next()) {

                int typeID = rs.getInt("TYPE_ID");
                String typeName = rs.getString("TYPE_NAME");
                String typeDesc = rs.getString("TYPE_DESC");

                Type currentType = new Type(typeName, typeDesc, typeID);

                typeList.add(currentType);
            }
            return typeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all Types", e);
        }
    }

    // Private helper methods
    private Type getTypeForName(String name) throws StorageException {
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = database.prepareStatement("SELECT * FROM TYPE WHERE TYPE_NAME=?");
            stmt.setString(1, name);

            rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by Name", e);
        }
    }

    private void deleteNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteNoteStmt = database.prepareStatement("DELETE FROM NOTE WHERE NOTE_TRANS_ID = ?");
            deleteNoteStmt.setInt(1, transactionID);
            deleteNoteStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting note for transaction ID", e);
        }
    }

    private void deleteAllTagToTransForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteTagToTransactionStmt = database.prepareStatement("DELETE FROM TAG_TO_TRANS WHERE TTTS_TRANS_ID = ?");
            deleteTagToTransactionStmt.setInt(1, transactionID);
            deleteTagToTransactionStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting tag_to_trans for transaction id", e);
        }
    }

    private Account getAccountForName(String name) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_NAME=?");
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }

    private Tag getTagForNameAndDescription(String tagName, String tagDescription) {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM TAG WHERE TAG_NAME=? AND TAG_DESC=?");
            stmt.setString(1, tagName);
            stmt.setString(2, tagDescription);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String resultTagName = rs.getString("TAG_NAME");
                String resultTagDesc = rs.getString("TAG_DESC");
                int id = rs.getInt("TAG_ID");
                return new Tag(resultTagName, resultTagDesc, id);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription) {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM PAYEE WHERE PAYEE_NAME=? AND PAYEE_DESC=?");
            stmt.setString(1, payeeName);
            stmt.setString(2, payeeDescription);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String newName = rs.getString("PAYEE_NAME");
                String description = rs.getString("PAYEE_DESC");
                int id = rs.getInt("PAYEE_ID");

                return new Payee(newName, description, id);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertTagToTrans(int tagID, int transID) {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TAG_TO_TRANS (TTTS_TAG_ID,TTTS_TRANS_ID) VALUES (?, ?)");
            stmt.setInt(1, tagID);
            stmt.setInt(2, transID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private Note getNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM NOTE WHERE NOTE_TRANS_ID=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String noteText = rs.getString("NOTE_TEXT");
                return new Note(transactionID, noteText);
            } else {
                return null;
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    private List<Tag> getTagsForTransactionID(int transactionID) throws StorageException {
        List<Tag> tags = new ArrayList<>();

        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM TAG_TO_TRANS WHERE TTTS_TRANS_ID=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tagId = rs.getInt("TTTS_TAG_ID");

                PreparedStatement tagStmt = database.prepareStatement("SELECT * FROM TAG WHERE TAG_ID = ?");
                tagStmt.setInt(1, tagId);

                ResultSet tagResults = tagStmt.executeQuery();

                while (tagResults.next()) {
                    String name = tagResults.getString("TAG_NAME");
                    String description = tagResults.getString("TAG_DESC");

                    Tag currentTag = new Tag(name, description, tagId);

                    tags.add(currentTag);
                }
            }
            return tags;

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    private Payee getPayeeForID(int payeeID) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM PAYEE WHERE PAYEE_ID=?");
            stmt.setInt(1, payeeID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String payeeName = rs.getString("PAYEE_NAME");
                String payeeDesc = rs.getString("PAYEE_DESC");
                return new Payee(payeeName, payeeDesc, payeeID);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    private Account getAccountForID(int accountID) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID=?");
            stmt.setInt(1, accountID);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by ID", e);
        }
    }

    private Type getTypeForID(int typeID) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM TYPE WHERE TYPE_ID=?");
            stmt.setInt(1, typeID);

            ResultSet rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by ID", e);
        }
    }

    private Type extractType(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            String newName = resultSet.getString("TYPE_NAME");
            String description = resultSet.getString("TYPE_DESC");
            int id = resultSet.getInt("TYPE_ID");

            return new Type(newName, description, id);
        } else {
            return null;
        }
    }

    private Account extractAccount(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String newName = rs.getString("ACCOUNT_NAME");
            String description = rs.getString("ACCOUNT_DESC");
            int id = rs.getInt("ACCOUNT_ID");
            return new Account(newName, description, id);
        } else {
            return null;
        }
    }
}
