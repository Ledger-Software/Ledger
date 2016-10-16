package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;
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
    @Override
    default void insertTransaction(Transaction transaction) throws StorageException {
        try {
            setDatabaseAutoCommit(false);

            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO TRANSACT (TRANS_DATETIME,TRANS_AMOUNT,TRANS_TYPE_ID,TRANS_PENDING,TRANS_ACCOUNT_ID,TRANS_PAYEE_ID) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());

                // Write existing type ID to java object
                transaction.getType().setId(existingType.getId());
            } else {
                throw new StorageException("Error while adding transaction: no such Type defined");
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForNameAndDescription(transaction.getAccount().getName(), transaction.getAccount().getDescription());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());

                // Write existing account ID to java object
                transaction.getAccount().setId(existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                stmt.setInt(5, transaction.getAccount().getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());

                // Write existing payee ID to java object
                transaction.getPayee().setId(existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                stmt.setInt(6, transaction.getPayee().getId());
            }

            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTransactionID = generatedIDs.getInt(1);

                // insert tags and tag-to-trans linkings
                for (Tag currentTag : transaction.getTagList()) {
                    Tag existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                    if (existingTag != null) {
                        insertTagToTrans(existingTag.getId(), insertedTransactionID);

                        // Write existing tag ID to java object
                        currentTag.setId(existingTag.getId());
                    } else {
                        insertTag(currentTag);
                        insertTagToTrans(currentTag.getId(), insertedTransactionID);
                    }
                }

                // insert note, if applicable
                if (transaction.getNote() != null) {
                    if (!transaction.getNote().getNoteText().equals("")) {
                        // Write inserted transaction ID to note object
                        transaction.getNote().setTransactionId(insertedTransactionID);

                        insertNote(transaction.getNote());
                    }
                }

                transaction.setId(insertedTransactionID);
            }

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

            PreparedStatement deleteTransactionStmt = getDatabase().prepareStatement("DELETE FROM TRANSACT WHERE TRANS_ID = ?");
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

            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE TRANSACT SET TRANS_DATETIME=?,TRANS_AMOUNT=?,TRANS_TYPE_ID=?,TRANS_PENDING=?,TRANS_ACCOUNT_ID=?,TRANS_PAYEE_ID=? WHERE TRANS_ID=?");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());

            Type existingType = getTypeForName(transaction.getType().getName());
            if (existingType != null) {
                stmt.setInt(3, existingType.getId());

                // Write existing type ID to java object
                transaction.getType().setId(existingType.getId());
            } else {
                throw new StorageException("Error while adding transaction: no such Type defined");
            }

            stmt.setBoolean(4, transaction.isPending());

            Account existingAccount = getAccountForNameAndDescription(transaction.getAccount().getName(), transaction.getAccount().getDescription());
            if (existingAccount != null) {
                stmt.setInt(5, existingAccount.getId());

                // Write existing account ID to java object
                transaction.getAccount().setId(existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                stmt.setInt(5, transaction.getAccount().getId());
            }


            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(6, existingPayee.getId());

                // Write existing payee ID to java object
                transaction.getPayee().setId(existingPayee.getId());
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

                    // Write existing tag ID to java object
                    currentTag.setId(existingTag.getId());
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
            ResultSet rs = stmt.executeQuery("SELECT TRANS_DATETIME, TRANS_ID, TRANS_TYPE_ID, TRANS_AMOUNT, TRANS_PENDING, TRANS_ACCOUNT_ID, TRANS_PAYEE_ID FROM TRANSACT;");

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

    // Private helper methods
    default Type getTypeForName(String name) throws StorageException {
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = getDatabase().prepareStatement("SELECT TYPE_ID, TYPE_NAME, TYPE_DESC FROM TYPE WHERE TYPE_NAME=?");
            stmt.setString(1, name);

            rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by Name", e);
        }
    }

    default void deleteNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteNoteStmt = getDatabase().prepareStatement("DELETE FROM NOTE WHERE NOTE_TRANS_ID = ?");
            deleteNoteStmt.setInt(1, transactionID);
            deleteNoteStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting note for transaction ID", e);
        }
    }

    default void deleteAllTagToTransForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement deleteTagToTransactionStmt = getDatabase().prepareStatement("DELETE FROM TAG_TO_TRANS WHERE TTTS_TRANS_ID = ?");
            deleteTagToTransactionStmt.setInt(1, transactionID);
            deleteTagToTransactionStmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting tag_to_trans for transaction id", e);
        }
    }

    default Account getAccountForNameAndDescription(String name, String description) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_DESC FROM ACCOUNT WHERE ACCOUNT_NAME=? AND ACCOUNT_DESC=?");
            stmt.setString(1, name);
            stmt.setString(2, description);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }

    default Tag getTagForNameAndDescription(String tagName, String tagDescription) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT TAG_ID FROM TAG WHERE TAG_NAME=? AND TAG_DESC=?");
            stmt.setString(1, tagName);
            stmt.setString(2, tagDescription);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String resultTagName = tagName;
                String resultTagDesc = tagDescription;
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

    default Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT PAYEE_ID FROM PAYEE WHERE PAYEE_NAME=? AND PAYEE_DESC=?");
            stmt.setString(1, payeeName);
            stmt.setString(2, payeeDescription);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String newName = payeeName;
                String description = payeeDescription;
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

    default void insertTagToTrans(int tagID, int transID) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO TAG_TO_TRANS (TTTS_TAG_ID,TTTS_TRANS_ID) VALUES (?, ?)");
            stmt.setInt(1, tagID);
            stmt.setInt(2, transID);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    default Note getNoteForTransactionID(int transactionID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT NOTE_TEXT FROM NOTE WHERE NOTE_TRANS_ID=?");
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

    default List<Tag> getTagsForTransactionID(int transactionID) throws StorageException {
        List<Tag> tags = new ArrayList<>();

        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT TTTS_TAG_ID FROM TAG_TO_TRANS WHERE TTTS_TRANS_ID=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tagId = rs.getInt("TTTS_TAG_ID");

                PreparedStatement tagStmt = getDatabase().prepareStatement("SELECT TAG_NAME, TAG_DESC FROM TAG WHERE TAG_ID = ?");
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

    default Payee getPayeeForID(int payeeID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT PAYEE_NAME, PAYEE_DESC FROM PAYEE WHERE PAYEE_ID=?");
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

    default Account getAccountForID(int accountID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_DESC FROM ACCOUNT WHERE ACCOUNT_ID=?");
            stmt.setInt(1, accountID);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by ID", e);
        }
    }

    default Type getTypeForID(int typeID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT TYPE_ID, TYPE_NAME, TYPE_DESC FROM TYPE WHERE TYPE_ID=?");
            stmt.setInt(1, typeID);

            ResultSet rs = stmt.executeQuery();

            return extractType(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Type by ID", e);
        }
    }

    default Type extractType(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            String newName = resultSet.getString("TYPE_NAME");
            String description = resultSet.getString("TYPE_DESC");
            int id = resultSet.getInt("TYPE_ID");

            return new Type(newName, description, id);
        } else {
            return null;
        }
    }

    default Account extractAccount(ResultSet rs) throws SQLException {
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
