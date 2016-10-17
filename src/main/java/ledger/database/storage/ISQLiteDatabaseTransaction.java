package ledger.database.storage;

import ledger.database.enity.*;
import ledger.database.storage.table.*;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by CJ on 10/11/2016.
 */
public interface ISQLiteDatabaseTransaction extends ISQLiteDatabase {
    // Basic CRUD functionality
    // Basic CRUD functionality
    @Override
    default void insertTransaction(Transaction transaction) throws StorageException {
        try {
            setDatabaseAutoCommit(false);

            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + TransactionTable.TABLE_NAME +
                    " (" + TransactionTable.TRANS_DATETIME +
                    "," + TransactionTable.TRANS_AMOUNT +
                    "," + TransactionTable.TRANS_TYPE_ID +
                    "," + TransactionTable.TRANS_PENDING +
                    "," + TransactionTable.TRANS_ACCOUNT_ID +
                    "," + TransactionTable.TRANS_PAYEE_ID +
                    ") VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                throw new StorageException("Error while adding transaction: no such Type defined");
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

            // Commit to DB
            getDatabase().commit();

        } catch (java.sql.SQLException e) {
            rollbackDatabase();
            throw new StorageException("Error while adding transaction", e);
        } finally {
            setDatabaseAutoCommit(true);
        }
    }

    @Override
    default void deleteTransaction(Transaction transaction) throws StorageException {
        try {
            setDatabaseAutoCommit(false);

            PreparedStatement deleteTransactionStmt = getDatabase().prepareStatement("DELETE FROM " + TransactionTable.TABLE_NAME +
                    " WHERE " + TransactionTable.TRANS_ID + " = ?");
            deleteTransactionStmt.setInt(1, transaction.getId());
            deleteTransactionStmt.executeUpdate();

            deleteNoteForTransactionID(transaction.getId());
            deleteAllTagToTransForTransactionID(transaction.getId());

            // Commit to DB
            getDatabase().commit();

        } catch (java.sql.SQLException e) {
            rollbackDatabase();
            throw new StorageException("Error while deleting transaction", e);
        } finally {
            setDatabaseAutoCommit(true);
        }
    }

    @Override
    default void editTransaction(Transaction transaction) throws StorageException {
        try {
            setDatabaseAutoCommit(false);

            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE " + TransactionTable.TABLE_NAME + " SET " +
                    TransactionTable.TRANS_DATETIME + "=?," +
                    TransactionTable.TRANS_AMOUNT + "=?," +
                    TransactionTable.TRANS_TYPE_ID + "=?," +
                    TransactionTable.TRANS_PENDING + "=?," +
                    TransactionTable.TRANS_ACCOUNT_ID + "=?," +
                    TransactionTable.TRANS_PAYEE_ID + "=? WHERE " +
                    TransactionTable.TRANS_ID + "=?");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());
            } else {
                throw new StorageException("Error while adding transaction: no such Type defined");
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

            // Commit to DB
            getDatabase().commit();

        } catch (java.sql.SQLException e) {
            rollbackDatabase();
            throw new StorageException("Error while editing transaction", e);
        } finally {
            setDatabaseAutoCommit(true);
        }
    }

    @Override
    default List<Transaction> getAllTransactions() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + TransactionTable.TABLE_NAME + ";");

            ArrayList<Transaction> transactionList = new ArrayList<>();

            while (rs.next()) {
                Date date = new Date(rs.getLong(TransactionTable.TRANS_DATETIME));
                int transactionID = rs.getInt(TransactionTable.TRANS_ID);
                int typeID = rs.getInt(TransactionTable.TRANS_TYPE_ID);
                int amount = rs.getInt(TransactionTable.TRANS_AMOUNT);
                boolean pending = rs.getBoolean(TransactionTable.TRANS_PENDING);
                int accountID = rs.getInt(TransactionTable.TRANS_ACCOUNT_ID);
                int payeeID = rs.getInt(TransactionTable.TRANS_PAYEE_ID);

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

    // Private helper methods
    default Type getTypeForName(String name) throws StorageException {
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = getDatabase().prepareStatement("SELECT * FROM " + TypeTable.TABLE_NAME +
                    " WHERE " + TypeTable.TYPE_NAME + "=?");
            stmt.setString(1, name);

            rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by Name", e);
        }
    }

    default void deleteNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteNoteStmt = getDatabase().prepareStatement("DELETE FROM " + NoteTable.TABLE_NAME +
                    " WHERE " + NoteTable.NOTE_TRANS_ID + " = ?");
            deleteNoteStmt.setInt(1, transactionID);
            deleteNoteStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting note for transaction ID", e);
        }
    }

    default void deleteAllTagToTransForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteTagToTransactionStmt = getDatabase().prepareStatement("DELETE FROM " + TagToTransTable.TABLE_NAME +
                    " WHERE " + TagToTransTable.TTTS_TRANS_ID + " = ?");
            deleteTagToTransactionStmt.setInt(1, transactionID);
            deleteTagToTransactionStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting tag_to_trans for transaction id", e);
        }
    }

    default Account getAccountForName(String name) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + AccountTable.TABLE_NAME +
                    " WHERE " + AccountTable.ACCOUNT_NAME + "=?");
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }

    default Tag getTagForNameAndDescription(String tagName, String tagDescription) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + TagTable.TABLE_NAME +
                    " WHERE " + TagTable.TAG_NAME + "=? AND " + TagTable.TAG_DESC + "=?");
            stmt.setString(1, tagName);
            stmt.setString(2, tagDescription);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String resultTagName = rs.getString(TagTable.TAG_NAME);
                String resultTagDesc = rs.getString(TagTable.TAG_DESC);
                int id = rs.getInt(TagTable.TAG_ID);
                return new Tag(resultTagName, resultTagDesc, id);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    default Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_NAME + "=? AND " + PayeeTable.PAYEE_DESC + "=?");
            stmt.setString(1, payeeName);
            stmt.setString(2, payeeDescription);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String newName = rs.getString(PayeeTable.PAYEE_NAME);
                String description = rs.getString(PayeeTable.PAYEE_DESC);
                int id = rs.getInt(PayeeTable.PAYEE_ID);

                return new Payee(newName, description, id);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    default void insertTagToTrans(int tagID, int transID) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + TagToTransTable.TABLE_NAME +
                    " (" + TagToTransTable.TTTS_TAG_ID + "," + TagToTransTable.TTTS_TRANS_ID + ") VALUES (?, ?)");
            stmt.setInt(1, tagID);
            stmt.setInt(2, transID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    default Note getNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + NoteTable.TABLE_NAME +
                    " WHERE " + NoteTable.NOTE_TRANS_ID + "=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String noteText = rs.getString(NoteTable.NOTE_TEXT);
                return new Note(transactionID, noteText);
            } else {
                return null;
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    default List<Tag> getTagsForTransactionID(int transactionID) throws StorageException {
        List<Tag> tags = new ArrayList<>();

        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + TagToTransTable.TABLE_NAME +
                    " WHERE " + TagToTransTable.TTTS_TRANS_ID + "=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tagId = rs.getInt(TagToTransTable.TTTS_TAG_ID);

                PreparedStatement tagStmt = getDatabase().prepareStatement("SELECT * FROM " + TagTable.TABLE_NAME +
                        " WHERE " + TagTable.TAG_ID + " = ?");
                tagStmt.setInt(1, tagId);

                ResultSet tagResults = tagStmt.executeQuery();

                while (tagResults.next()) {
                    String name = tagResults.getString(TagTable.TAG_NAME);
                    String description = tagResults.getString(TagTable.TAG_DESC);

                    Tag currentTag = new Tag(name, description, tagId);

                    tags.add(currentTag);
                }
            }
            return tags;

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    default Payee getPayeeForID(int payeeID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_ID + "=?");
            stmt.setInt(1, payeeID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String payeeName = rs.getString(PayeeTable.PAYEE_NAME);
                String payeeDesc = rs.getString(PayeeTable.PAYEE_DESC);
                return new Payee(payeeName, payeeDesc, payeeID);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    default Account getAccountForID(int accountID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + AccountTable.TABLE_NAME +
                    " WHERE " + AccountTable.ACCOUNT_ID + "=?");
            stmt.setInt(1, accountID);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by ID", e);
        }
    }

    default Type getTypeForID(int typeID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT * FROM " + TypeTable.TABLE_NAME +
                    " WHERE " + TypeTable.TYPE_ID + "=?");
            stmt.setInt(1, typeID);

            ResultSet rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by ID", e);
        }
    }

    default Type extractType(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            String newName = resultSet.getString(TypeTable.TYPE_NAME);
            String description = resultSet.getString(TypeTable.TYPE_DESC);
            int id = resultSet.getInt(TypeTable.TYPE_ID);

            return new Type(newName, description, id);
        } else {
            return null;
        }
    }

    default Account extractAccount(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String newName = rs.getString(AccountTable.ACCOUNT_NAME);
            String description = rs.getString(AccountTable.ACCOUNT_DESC);
            int id = rs.getInt(AccountTable.ACCOUNT_ID);
            return new Account(newName, description, id);
        } else {
            return null;
        }
    }

}
