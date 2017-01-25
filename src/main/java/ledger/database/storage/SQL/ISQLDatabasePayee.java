package ledger.database.storage.SQL;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.PayeeTable;
import ledger.database.storage.table.TagTable;
import ledger.database.storage.table.TagToPayeeTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLDatabasePayee extends ISQLiteDatabase {

    @Override
    default void insertPayee(Payee payee) throws StorageException {
        try {

            PreparedStatement checkIfExistsStmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_ID + ", "
                    + PayeeTable.PAYEE_NAME + ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME + " WHERE " + PayeeTable.PAYEE_NAME + "=?");
            checkIfExistsStmt.setString(1, payee.getName());

            ResultSet existingPayees = checkIfExistsStmt.executeQuery();
            if (existingPayees.next()) {
                int payeeID = existingPayees.getInt(PayeeTable.PAYEE_ID);
                String payeeName = existingPayees.getString(PayeeTable.PAYEE_NAME);
                String payeeDescription = existingPayees.getString(PayeeTable.PAYEE_DESC);

                payee.setId(payeeID);
                payee.setName(payeeName);
                payee.setDescription(payeeDescription);
                payee.setTags(getAllTagsForPayee(payee));
                return;
            }

            PreparedStatement stmt =
                    getDatabase().prepareStatement("INSERT INTO " + PayeeTable.TABLE_NAME +
                            " (" + PayeeTable.PAYEE_NAME +
                            ", " + PayeeTable.PAYEE_DESC +
                            ") VALUES (?, ?)");
            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedPayeeID = generatedIDs.getInt(1);
                payee.setId(insertedPayeeID);
            }

            if (payee.getTags() != null) {
                for(Tag t : payee.getTags()) {
                    lookupAndInsertTag(t);
                    addTagForPayee(t, payee);
                }
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }

    @Override
    default void deletePayee(Payee payee) throws StorageException {
        try {
            deleteAllTagsForPayee(payee);
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + PayeeTable.TABLE_NAME +
                    " WHERE " + PayeeTable.PAYEE_ID + " = ?");
            stmt.setInt(1, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Payee", e);
        }
    }

    @Override
    default void editPayee(Payee payee) throws StorageException {
        try {
            getDatabase().setAutoCommit(false);

            PreparedStatement stmt =
                    getDatabase().prepareStatement("UPDATE " + PayeeTable.TABLE_NAME +
                            " SET " + PayeeTable.PAYEE_NAME +
                            " = ?, " + PayeeTable.PAYEE_DESC +
                            " = ? WHERE " + PayeeTable.PAYEE_ID + " = ?");

            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.setInt(3, payee.getId());

            if (payee.getTags() != null) {
                deleteAllTagsForPayee(payee);
                for(Tag t : payee.getTags()) {
                    lookupAndInsertTag(t);
                    addTagForPayee(t, payee);
                }
            }

            stmt.executeUpdate();
            getDatabase().commit();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Payee", e);
        } finally {
            setDatabaseAutoCommit(true);
        }
    }

    @Override
    default List<Payee> getAllPayees() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + PayeeTable.PAYEE_ID +
                    ", " + PayeeTable.PAYEE_NAME +
                    ", " + PayeeTable.PAYEE_DESC +
                    " FROM " + PayeeTable.TABLE_NAME + ";");

            ArrayList<Payee> payeeList = new ArrayList<>();

            while (rs.next()) {

                int payeeID = rs.getInt(PayeeTable.PAYEE_ID);
                String payeeName = rs.getString(PayeeTable.PAYEE_NAME);
                String payeeDesc = rs.getString(PayeeTable.PAYEE_DESC);
                List<Tag> tags = getAllTagsForPayeeId(payeeID);

                Payee currentPayee = new Payee(payeeName, payeeDesc, payeeID, tags);

                payeeList.add(currentPayee);
            }
            return payeeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

    @Override
    default List<Tag> getAllTagsForPayee(Payee payee) throws StorageException {
        int id = payee.getId();
        if(id == -1) {
            insertPayee(payee);
        }
        return getAllTagsForPayeeId(payee.getId());
    }

    @Override
    default List<Tag> getAllTagsForPayeeId(int payeeId) throws StorageException{
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_ID +
                    ", " + TagTable.TAG_NAME +
                    ", " + TagTable.TAG_DESC +
                    " FROM " + TagTable.TABLE_NAME + " WHERE " + TagTable.TAG_ID + " IN (SELECT " + TagToPayeeTable.TTPE_TAG_ID +

                    " FROM " + TagToPayeeTable.TABLE_NAME +
                    " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID + " =?)");
            stmt.setInt(1, payeeId);
            ResultSet rs = stmt.executeQuery();
            ArrayList<Tag> tags = new ArrayList<>();

            while (rs.next()) {
                int tagID = rs.getInt(TagTable.TAG_ID);
                String tagName = rs.getString(TagTable.TAG_NAME);
                String tagDescription = rs.getString(TagTable.TAG_DESC);

                Tag currentTag = new Tag(tagName, tagDescription, tagID);

                tags.add(currentTag);
            }
            return tags;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all tags for a payee", e);
        }
    }

    @Override
    default void deleteTagForPayee(Tag tag, Payee payee) throws StorageException {
        payee = lookupAndInsertPayee(payee);
        tag = lookupAndInsertTag(tag);
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + TagToPayeeTable.TABLE_NAME + " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID + " =? AND " + TagToPayeeTable.TTPE_TAG_ID + "=?");
            stmt.setInt(1, payee.getId());
            stmt.setInt(2, tag.getId());
            stmt.execute();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting a tag for a payee", e);
        }
    }

    @Override
    default void deleteAllTagsForPayee(Payee p) throws StorageException{
        Payee payee = lookupAndInsertPayee(p);
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + TagToPayeeTable.TABLE_NAME + " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID + " =?");
            stmt.setInt(1, payee.getId());
            stmt.execute();
        } catch (java.sql.SQLException e) {
            if (payee == null)  throw new StorageException("Error while deleting all tags for payee", e);
            else throw new StorageException("Error while deleting all tags for Payee " + payee.getName(), e);
        }
    }

    @Override
    default void addTagForPayee(Tag tag, Payee payee) throws StorageException {
        payee = lookupAndInsertPayee(payee);
        tag = lookupAndInsertTag(tag);
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + TagToPayeeTable.TABLE_NAME + " ( " + TagToPayeeTable.TTPE_TAG_ID + ", " + TagToPayeeTable.TTPE_PAYEE_ID + " ) VALUES (?,?)");
            stmt.setInt(1, tag.getId());
            stmt.setInt(2, payee.getId());
            stmt.execute();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding a tag for a payee", e);
        }
    }

    default Tag lookupAndInsertTag(Tag currentTag) throws StorageException {
        Tag existingTag;
        if (currentTag.getId() != -1) {
            existingTag = currentTag;
        } else {
            existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
        }
        if (existingTag != null) {
            currentTag.setId(existingTag.getId());
        } else {
            insertTag(currentTag);
            currentTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
        }
        return currentTag;
    }

    default Payee lookupAndInsertPayee(Payee currentPayee) throws StorageException {
        Payee existingPayee;
        if (currentPayee.getId() != -1) {
            existingPayee = currentPayee;
        } else {
            existingPayee = getPayeeForNameAndDescription(currentPayee.getName(), currentPayee.getDescription());
        }
        if (existingPayee != null) {
            currentPayee.setId(existingPayee.getId());
        } else {
            insertPayee(currentPayee);
            currentPayee = getPayeeForNameAndDescription(currentPayee.getName(), currentPayee.getDescription());
        }
        return currentPayee;
    }

}
