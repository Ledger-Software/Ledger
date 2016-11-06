package ledger.database.storage.SQL;

import ledger.database.entity.Tag;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.TagTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLDatabaseTag extends ISQLiteDatabase {

    @Override
    default void insertTag(Tag tag) throws StorageException {
        try {

            PreparedStatement checkIfExistsStmt = getDatabase().prepareStatement("SELECT " + TagTable.TAG_NAME +
                    " FROM " + TagTable.TABLE_NAME + " WHERE " + TagTable.TAG_NAME + "=?");
            checkIfExistsStmt.setString(1, tag.getName());

            System.out.println(checkIfExistsStmt.toString());

            checkIfExistsStmt.executeQuery();
            ResultSet existingPayees = checkIfExistsStmt.getGeneratedKeys();
            if (existingPayees.next()) {
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
            throw new StorageException("Error while getting all notes", e);
        }
    }

}
