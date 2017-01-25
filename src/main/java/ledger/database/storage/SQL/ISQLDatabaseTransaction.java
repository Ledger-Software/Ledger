package ledger.database.storage.SQL;

import ledger.database.entity.*;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.*;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ISQLDatabaseTransaction extends ISQLiteDatabase {

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

            lookupAndSetTypeForSQLStatement(transaction, stmt, 3);

            stmt.setBoolean(4, transaction.isPending());

            lookupAndSetAccountForSQLStatement(transaction, stmt, 5);

            lookupAndSetPayeeForSQLStatement(transaction, stmt, 6);

            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTransactionID = generatedIDs.getInt(1);

                // insert tags and tag-to-trans linkings
                for (Tag currentTag : transaction.getTags()) {
                    lookupAndInsertTag(currentTag, insertedTransactionID);
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
        } catch (NullPointerException e) {
            rollbackDatabase();
            throw new StorageException("Error while adding transaction. Not all necessary fields were given.", e);
        } finally {
            setDatabaseAutoCommit(true);
        }
    }

    @Override
    default void deleteTransaction(Transaction transaction) throws StorageException {
        try {
            setDatabaseAutoCommit(false);

            //First delete the corresponding Note
            PreparedStatement noteStatement = getDatabase().prepareStatement("DELETE FROM " + NoteTable.TABLE_NAME + " WHERE "
                    + NoteTable.NOTE_TRANS_ID + " = ?");
            noteStatement.setInt(1, transaction.getId());
            noteStatement.executeUpdate();

            //Delete Any Entries in TAG_TO_TRANS
            PreparedStatement tttsStatement = getDatabase().prepareStatement("DELETE FROM " + TagToTransTable.TABLE_NAME + " WHERE "
                    + TagToTransTable.TTTS_TRANS_ID + " = ?");
            tttsStatement.setInt(1, transaction.getId());
            tttsStatement.executeUpdate();

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

            lookupAndSetTypeForSQLStatement(transaction, stmt, 3);

            stmt.setBoolean(4, transaction.isPending());

            lookupAndSetAccountForSQLStatement(transaction, stmt, 5);

            lookupAndSetPayeeForSQLStatement(transaction, stmt, 6);

            stmt.setInt(7, transaction.getId());

            stmt.executeUpdate();

            // Update tag associations
            deleteAllTagToTransForTransactionID(transaction.getId());
            for (Tag currentTag : transaction.getTags()) {
                lookupAndInsertTag(currentTag, transaction.getId());
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
            ResultSet rs = stmt.executeQuery("SELECT " + TransactionTable.TRANS_DATETIME +
                    ", " + TransactionTable.TRANS_ID +
                    ", " + TransactionTable.TRANS_TYPE_ID +
                    ", " + TransactionTable.TRANS_AMOUNT +
                    ", " + TransactionTable.TRANS_PENDING +
                    ", " + TransactionTable.TRANS_ACCOUNT_ID +
                    ", " + TransactionTable.TRANS_PAYEE_ID +
                    " FROM " + TransactionTable.TABLE_NAME +
                    ";");

            return extractTransactions(rs);

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all transactions", e);
        }
    }

    @Override
    default List<Transaction> getAllTransactionsForAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TransactionTable.TRANS_DATETIME +
                    ", " + TransactionTable.TRANS_ID +
                    ", " + TransactionTable.TRANS_TYPE_ID +
                    ", " + TransactionTable.TRANS_AMOUNT +
                    ", " + TransactionTable.TRANS_PENDING +
                    ", " + TransactionTable.TRANS_ACCOUNT_ID +
                    ", " + TransactionTable.TRANS_PAYEE_ID +
                    " FROM " + TransactionTable.TABLE_NAME +
                    " WHERE " + TransactionTable.TRANS_ACCOUNT_ID + "=?;");
            stmt.setInt(1, account.getId());
            ResultSet rs = stmt.executeQuery();

            return extractTransactions(rs);

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all transactions", e);
        }
    }

    default List<Transaction> extractTransactions(ResultSet rs) throws SQLException, StorageException {
        List<Transaction> transactionList = new ArrayList<>();

        while (rs.next()) {
            Date date = new Date(rs.getLong(TransactionTable.TRANS_DATETIME));
            int transactionID = rs.getInt(TransactionTable.TRANS_ID);
            int typeID = rs.getInt(TransactionTable.TRANS_TYPE_ID);
            int amount = rs.getInt(TransactionTable.TRANS_AMOUNT);
            boolean pending = rs.getBoolean(TransactionTable.TRANS_PENDING);
            int accountID = rs.getInt(TransactionTable.TRANS_ACCOUNT_ID);
            int payeeID = rs.getInt(TransactionTable.TRANS_PAYEE_ID);

            Type type = getTypeForID(typeID);
            Account transAccount = getAccountForID(accountID);
            Payee payee = getPayeeForID(payeeID);
            List<Tag> tags = getTagsForTransactionID(transactionID);
            Note note = getNoteForTransactionID(transactionID);

            Transaction currentTransaction = new Transaction(date, type, amount, transAccount, payee, pending, tags, note, transactionID);

            transactionList.add(currentTransaction);
        }
        return transactionList;
    }

    // Private helper methods
    default Type getTypeForName(String name) throws StorageException {
        PreparedStatement stmt;
        ResultSet rs;

        try {
            stmt = getDatabase().prepareStatement("SELECT " + TypeTable.TYPE_ID +
                    ", " + TypeTable.TYPE_NAME +
                    ", " + TypeTable.TYPE_DESC +
                    " FROM " + TypeTable.TABLE_NAME +
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

    default Account getAccountForNameAndDescription(String name, String description) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + AccountTable.ACCOUNT_ID +
                    ", " + AccountTable.ACCOUNT_NAME +
                    ", " + AccountTable.ACCOUNT_DESC +
                    " FROM " + AccountTable.TABLE_NAME +
                    " WHERE " + AccountTable.ACCOUNT_NAME + "=? AND " + AccountTable.ACCOUNT_DESC + "=?");

            stmt.setString(1, name);
            stmt.setString(2, description);

            ResultSet rs = stmt.executeQuery();

            return extractAccount(rs);
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }

    @Override
    default Tag getTagForNameAndDescription(String tagName, String tagDescription) {
        try {

            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_ID +
                    " FROM " + TagTable.TABLE_NAME +
                    " WHERE " + TagTable.TAG_NAME + "=? AND " + TagTable.TAG_DESC + "=?");
            stmt.setString(1, tagName);
            stmt.setString(2, tagDescription);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String resultTagName = tagName;
                String resultTagDesc = tagDescription;
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

    @Override
    default Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription) {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_ID +
                    " FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_NAME + "=? AND " + PayeeTable.PAYEE_DESC + "=?");
            stmt.setString(1, payeeName);
            stmt.setString(2, payeeDescription);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String newName = payeeName;
                String description = payeeDescription;
                int id = rs.getInt(PayeeTable.PAYEE_ID);
                List<Tag> tags = getAllTagsForPayeeId(id);

                return new Payee(newName, description, id, tags);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } catch (StorageException e) {
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
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + NoteTable.NOTE_TEXT +
                    " FROM " + NoteTable.TABLE_NAME +
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
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TagToTransTable.TTTS_TAG_ID +
                    " FROM " + TagToTransTable.TABLE_NAME +
                    " WHERE " + TagToTransTable.TTTS_TRANS_ID + "=?");
            stmt.setInt(1, transactionID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tagId = rs.getInt(TagToTransTable.TTTS_TAG_ID);

                PreparedStatement tagStmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_NAME +
                        ", " + TagTable.TAG_DESC +
                        " FROM " + TagTable.TABLE_NAME +
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
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_NAME +
                    ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_ID + "=?");
            stmt.setInt(1, payeeID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String payeeName = rs.getString(PayeeTable.PAYEE_NAME);
                String payeeDesc = rs.getString(PayeeTable.PAYEE_DESC);
                List<Tag> tags = getAllTagsForPayeeId(payeeID);
                return new Payee(payeeName, payeeDesc, payeeID, tags);
            } else {
                return null;
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Note by AccountID", e);
        }
    }

    default Account getAccountForID(int accountID) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + AccountTable.ACCOUNT_ID +
                    ", " + AccountTable.ACCOUNT_NAME +
                    ", " + AccountTable.ACCOUNT_DESC +
                    " FROM " + AccountTable.TABLE_NAME +
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
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TypeTable.TYPE_ID +
                    ", " + TypeTable.TYPE_NAME +
                    ", " + TypeTable.TYPE_DESC +
                    " FROM " + TypeTable.TABLE_NAME +
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

    default void lookupAndSetTypeForSQLStatement(Transaction transaction, PreparedStatement stmt, int statementInputIndex) throws StorageException {
        try {
            Type existingType;
            if (transaction.getType().getId() != -1) {
                existingType = transaction.getType();
            } else {
                existingType = getTypeForName(transaction.getType().getName());
            }
            if (existingType != null) {
                stmt.setInt(statementInputIndex, existingType.getId());

                // Write existing type ID to java object
                transaction.setType(existingType);
            } else {
                throw new StorageException("Error while adding transaction: no such Type defined");
            }
        } catch (SQLException e) {
            throw new StorageException("Error while looking up type for SQL statement", e);
        }
    }

    default void lookupAndSetAccountForSQLStatement(Transaction transaction, PreparedStatement stmt, int statementInputIndex) throws StorageException {
        try {
            Account existingAccount;
            if (transaction.getAccount().getId() != -1) {
                existingAccount = transaction.getAccount();
            } else {
                existingAccount = getAccountForNameAndDescription(transaction.getAccount().getName(), transaction.getAccount().getDescription());
            }
            if (existingAccount != null) {
                stmt.setInt(statementInputIndex, existingAccount.getId());

                // Write existing account ID to java object
                transaction.setAccount(existingAccount);
            } else {
                insertAccount(transaction.getAccount());
                stmt.setInt(statementInputIndex, transaction.getAccount().getId());
            }
        } catch (SQLException e) {
            throw new StorageException("Error while looking up account for SQL statement", e);
        }
    }

    default void lookupAndSetPayeeForSQLStatement(Transaction transaction, PreparedStatement stmt, int statementInputIndex) throws StorageException {
        try {
            Payee existingPayee;
            if (transaction.getPayee().getId() != -1) {
                existingPayee = transaction.getPayee();
            } else {
                existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            }
            if (existingPayee != null) {
                stmt.setInt(statementInputIndex, existingPayee.getId());

                // Write existing payee ID to java object
                transaction.setPayee(existingPayee);
            } else {
                insertPayee(transaction.getPayee());
                stmt.setInt(statementInputIndex, transaction.getPayee().getId());
            }
        } catch (SQLException e) {
            throw new StorageException("Error while looking up payee for SQL statement", e);
        }
    }

    default void lookupAndInsertTag(Tag currentTag, int insertedTransactionID) throws StorageException {
        Tag existingTag;
        if (currentTag.getId() != -1) {
            existingTag = currentTag;
        } else {
            existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
        }
        if (existingTag != null) {
            insertTagToTrans(existingTag.getId(), insertedTransactionID);

            // Write existing tag ID to java object
            currentTag.setId(existingTag.getId());
        } else {
            insertTag(currentTag);
            insertTagToTrans(currentTag.getId(), insertedTransactionID);
        }
    }
}
