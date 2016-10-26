package ledger.database.storage.SQL;

import ledger.database.enity.Transaction;
import ledger.database.enity.Type;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.TypeTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface ISQLDatabaseType extends ISQLiteDatabase {
    @Override
    default void insertType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + TypeTable.TABLE_NAME +
                    " (" + TypeTable.TYPE_NAME + ", " + TypeTable.TYPE_DESC + ") VALUES (?, ?)");
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
            List<Transaction> trans = getAllTransactions();
            List<Type> types = trans.stream().map(Transaction::getType).collect(Collectors.toList());
            List<Integer> ids = types.stream().map(Type::getId).collect(Collectors.toList());
            if (ids.contains(type.getId())) throw new StorageException("Cannot delete a Type while used by Transaction");

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
            ResultSet rs = stmt.executeQuery("SELECT TYPE_ID, TYPE_NAME, TYPE_DESC FROM TYPE;");

            List<Type> typeList = new ArrayList<>();

            while (rs.next()) {

                int typeID = rs.getInt(TypeTable.TYPE_ID);
                String typeName = rs.getString(TypeTable.TYPE_NAME);
                String typeDesc = rs.getString(TypeTable.TYPE_DESC);

                Type currentType = new Type(typeName, typeDesc, typeID);

                typeList.add(currentType);
            }
            return typeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all Types", e);
        }
    }

}
