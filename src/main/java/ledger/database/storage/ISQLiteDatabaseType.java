package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.Type;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJ on 10/11/2016.
 */
public interface ISQLiteDatabaseType extends ISQLiteDatabase {
    @Override
    default void insertType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO TYPE (TYPE_NAME,TYPE_DESC) VALUES (?, ?)");
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
    default void deleteType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM TYPE WHERE TYPE_ID = ?");
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Type", e);
        }
    }

    @Override
    default void editType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE TYPE SET TYPE_NAME=?, TYPE_DESC=? WHERE TYPE_ID=?");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing type");
        }
    }


    @Override
    default List<Type> getAllTypes() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
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

}
