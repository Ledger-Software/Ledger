package ledger.database.storage.SQL;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.PayeeTable;
import ledger.database.storage.table.TagTable;
import ledger.database.storage.table.TagToPayeeTable;
import ledger.database.storage.table.TagToTransTable;
import ledger.exception.StorageException;
import org.h2.command.Prepared;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    default List<Tag> getAllTags() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + TagTable.TAG_ID +
                    ", " + TagTable.TAG_NAME +
                    ", " + TagTable.TAG_DESC +
                    " FROM " + TagTable.TABLE_NAME + ";");

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
            throw new StorageException("Error while getting all tags", e);
        }
    }
    @Override
    default List<Tag> getAllTagsForPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_ID +
                            ", " + TagTable.TAG_NAME +
                            ", " + TagTable.TAG_DESC +
                            " FROM " + TagTable.TABLE_NAME +" WHERE " + TagTable.TAG_ID + " IN (SELECT " + TagToPayeeTable.TTPE_TAG_ID +

                    " FROM " + TagToPayeeTable.TABLE_NAME +
                    " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID +" =?)");
            stmt.setInt(1, lookupAndInsertPayee(payee).getId());
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
    default List<Tag> getAllTagsNotForPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_ID +
                    ", " + TagTable.TAG_NAME +
                    ", " + TagTable.TAG_DESC +
                    " FROM " + TagTable.TABLE_NAME +" WHERE " + TagTable.TAG_ID + " NOT IN (SELECT " + TagToPayeeTable.TTPE_TAG_ID +

                    " FROM " + TagToPayeeTable.TABLE_NAME +
                    " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID +" =?)");
            stmt.setInt(1, lookupAndInsertPayee(payee).getId());
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
    default void deleteTagForPayee(Tag tag, Payee payee) throws StorageException{
       payee = lookupAndInsertPayee(payee);
       tag = lookupAndInsertTag(tag);
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + TagToPayeeTable.TABLE_NAME + " WHERE " + TagToPayeeTable.TTPE_PAYEE_ID + " =? AND " + TagToPayeeTable.TTPE_TAG_ID + "=?");
            stmt.setInt(1,payee.getId());
            stmt.setInt(2, tag.getId());
            stmt.execute();
        } catch (java.sql.SQLException e){
            throw new StorageException("Error while deleting a tag for a payee", e);
        }
    }
    @Override
    default void addTagForPayee(Tag tag, Payee payee) throws StorageException{
        payee = lookupAndInsertPayee(payee);
        tag = lookupAndInsertTag(tag);
        try {
            PreparedStatement stmt  = getDatabase().prepareStatement("INSERT INTO " + TagToPayeeTable.TABLE_NAME + " ( " +TagToPayeeTable.TTPE_TAG_ID + ", " +TagToPayeeTable.TTPE_PAYEE_ID +" ) VALUES (?,?)" );
            stmt.setInt(1, tag.getId());
            stmt.setInt(2, payee.getId());
            stmt.execute();
        } catch (java.sql.SQLException e){
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
