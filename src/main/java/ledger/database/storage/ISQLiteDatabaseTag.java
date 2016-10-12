package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.Tag;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJ on 10/11/2016.
 */
public interface ISQLiteDatabaseTag extends ISQLiteDatabase {

    @Override
    default void insertTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO TAG (TAG_NAME, TAG_DESC) VALUES (?, ?)");
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
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM TAG WHERE TAG_ID = ?");
            stmt.setInt(1, tag.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Tag", e);
        }
    }

    @Override
    default void editTag(Tag tag) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE TAG SET TAG_NAME=?, TAG_DESC=? WHERE TAG_ID=?");
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

}
