package ledger.database.storage.SQL;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.TagTable;
import ledger.database.storage.table.TagToPayeeTable;
import ledger.database.storage.table.TagToTransTable;
import ledger.exception.StorageException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLDatabaseTag extends ISQLiteDatabase {

    @Override
    default void insertTag(Tag tag) throws StorageException {
        try {

            PreparedStatement checkIfExistsStmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_ID + ", " + TagTable.TAG_NAME +
                    ", " + TagTable.TAG_DESC + " FROM " + TagTable.TABLE_NAME + " WHERE " + TagTable.TAG_NAME + "=?");
            checkIfExistsStmt.setString(1, tag.getName());

            ResultSet existingTags = checkIfExistsStmt.executeQuery();
            if (existingTags.next()) {
                int id = existingTags.getInt(TagTable.TAG_ID);
                String name = existingTags.getString(TagTable.TAG_NAME);
                String description = existingTags.getString(TagTable.TAG_DESC);

                tag.setId(id);
                tag.setName(name);
                tag.setDescription(description);

                return;
            }

            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + TagTable.TABLE_NAME +
                    " (" + TagTable.TAG_NAME + ", " + TagTable.TAG_DESC + ") VALUES (?, ?)");
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
    default void deleteTag(Tag tag) throws StorageException {
        try {
            PreparedStatement tttsDelete = getDatabase().prepareStatement("DELETE FROM " + TagToTransTable.TABLE_NAME +
                    " WHERE " + TagToTransTable.TTTS_TAG_ID + "=?");
            tttsDelete.setInt(1, tag.getId());
            tttsDelete.executeUpdate();

            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + TagTable.TABLE_NAME +
                    " WHERE " + TagTable.TAG_ID + " = ?");
            stmt.setInt(1, tag.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Tag", e);
        }
    }

    @Override
    default void editTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE " + TagTable.TABLE_NAME +
                    " SET " + TagTable.TAG_NAME + "=?, " + TagTable.TAG_DESC + "=? WHERE " + TagTable.TAG_ID + "=?");
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getDescription());
            stmt.setInt(3, tag.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Tag", e);
        }
    }

    @Override
    default void mergeTags(List<Tag> tags) throws StorageException {
        if (tags.size() < 2) {
            throw new StorageException("Error while merging tags: merging requires at least two tags");
        }

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = getDatabase().getAutoCommit();
            getDatabase().setAutoCommit(false);

            Tag mergeTarget = tags.get(0);
            List<Tag> mergeSources = tags.subList(1, tags.size());

            // Set up beginning of update statement for TagToTransTable
            String updateTagToTransSQL = "UPDATE " + TagToTransTable.TABLE_NAME + " SET " + TagToTransTable.TTTS_TAG_ID + " = ? WHERE ";

            // Set up beginning of update statement for TagToPayeeTable
            String updateTagToPayeeSQL = "UPDATE " + TagToPayeeTable.TABLE_NAME + " SET " + TagToPayeeTable.TTPE_TAG_ID + " = ? WHERE ";

            // Set up beginning of delete statement for Tag table
            String deleteMergedTagsSQL = "DELETE FROM " + TagTable.TABLE_NAME +
                    " WHERE ";

            // Add n conditionals to all three statements w/ SQL OR
            for (int i = 0; i < mergeSources.size(); i++) {
                if (i != 0) {
                    updateTagToTransSQL += " OR ";
                    updateTagToPayeeSQL += " OR ";
                    deleteMergedTagsSQL += " OR ";
                }
                updateTagToTransSQL += TagToTransTable.TTTS_TAG_ID + " = ?";
                updateTagToPayeeSQL += TagToPayeeTable.TTPE_TAG_ID + " = ?";
                deleteMergedTagsSQL += TagTable.TAG_ID + " = ?";
            }

            // Prepare statements
            PreparedStatement stmt1 = getDatabase().prepareStatement(updateTagToTransSQL);
            PreparedStatement stmt2 = getDatabase().prepareStatement(updateTagToPayeeSQL);
            PreparedStatement stmt3 = getDatabase().prepareStatement(deleteMergedTagsSQL);

            // Set merge target tag ID as first parameterin update statements
            stmt1.setInt(1, mergeTarget.getId());
            stmt2.setInt(1, mergeTarget.getId());

            int paramIndex = 2;
            // Set merge source tag IDs as remaining parameters in all statements
            for (int i = 0; i < mergeSources.size(); i++) {
                Tag currentMergeSource = mergeSources.get(i);
                stmt1.setInt(paramIndex, currentMergeSource.getId());
                stmt2.setInt(paramIndex, currentMergeSource.getId());
                stmt3.setInt(paramIndex - 1, currentMergeSource.getId());
                paramIndex++;
            }

            stmt1.execute();
            stmt2.execute();
            stmt3.execute();
        } catch (Exception e) {
            rollbackDatabase();
            throw new StorageException("Error while merging tags", e);
        } finally {
            setDatabaseAutoCommit(originalAutoCommit);
        }
    }

    @Override
    default List<Tag> getAllTags() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + TagTable.TAG_ID +
                    ", " + TagTable.TAG_NAME +
                    ", " + TagTable.TAG_DESC +
                    " FROM " + TagTable.TABLE_NAME + ";");

            ArrayList<Tag> tags = new ArrayList<>();

            extractTags(rs, tags);
            return tags;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all tags", e);
        }
    }

    @Override
    default List<Tag> getAllTagsForPayeeId(int payeeId) throws StorageException {
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

            extractTags(rs, tags);
            return tags;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all tags for a payee", e);
        }
    }

    default void extractTags(ResultSet rs, ArrayList<Tag> tags) throws SQLException {
        while (rs.next()) {
            int tagID = rs.getInt(TagTable.TAG_ID);
            String tagName = rs.getString(TagTable.TAG_NAME);
            String tagDescription = rs.getString(TagTable.TAG_DESC);

            Tag currentTag = new Tag(tagName, tagDescription, tagID);

            tags.add(currentTag);
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
    default void deleteAllTagsForPayee(Payee p) throws StorageException {
        Payee payee = lookupAndInsertPayee(p);
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + TagToPayeeTable.TABLE_NAME + " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID + " =?");
            stmt.setInt(1, payee.getId());
            stmt.execute();
        } catch (java.sql.SQLException e) {
            if (payee == null) throw new StorageException("Error while deleting all tags for payee", e);
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

}
