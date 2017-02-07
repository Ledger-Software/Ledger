package ledger.database.storage.SQL;

import ledger.database.entity.IgnoredExpression;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.IgnoredExpressionTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gert on 2/7/17.
 */
public interface ISQLDatabaseIgnoredExpression extends ISQLiteDatabase{

    @Override
    default void insertIgnoredExpression(IgnoredExpression igEx) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + IgnoredExpressionTable.TABLE_NAME +
                    " (" + IgnoredExpressionTable.IGNORE_STRING +
                    ", " + IgnoredExpressionTable.MATCH_OR_CONTAIN +
                    ") VALUES (?,?)");
            stmt.setString(1, igEx.getExpression());
            stmt.setBoolean(2, igEx.getMatch());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while inserting an Ignored Expression");
        }
    }
    @Override
    default void deleteIgnoredExpression(IgnoredExpression igEx) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + IgnoredExpressionTable.TABLE_NAME +
                    " WHERE " + IgnoredExpressionTable.IGNORE_ID + " = ?");
            stmt.setInt(1, igEx.getExpressionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting an Ignored Expression");
        }
    }

    @Override
    default void editIgnoredExpression(IgnoredExpression igEx) throws StorageException {
        try {
            PreparedStatement stmt =
                    getDatabase().prepareStatement("UPDATE " + IgnoredExpressionTable.TABLE_NAME +
                            " SET " + IgnoredExpressionTable.MATCH_OR_CONTAIN + "=?, " +
                            IgnoredExpressionTable.MATCH_OR_CONTAIN +"=? WHERE " + IgnoredExpressionTable.IGNORE_ID + " = ?");

            stmt.setString(1, igEx.getExpression());
            stmt.setBoolean(2, igEx.getMatch());
            stmt.setInt(3,igEx.getExpressionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing an Ignored Expression");
        }
    }

    @Override
    default List<IgnoredExpression> getAllIgnoredExpressions() throws StorageException{
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + IgnoredExpressionTable.IGNORE_ID +
                    ", " + IgnoredExpressionTable.IGNORE_STRING +
                    ", " + IgnoredExpressionTable.MATCH_OR_CONTAIN +
                    " FROM " + IgnoredExpressionTable.TABLE_NAME + ";");

            ArrayList<IgnoredExpression> ignoreList = new ArrayList<>();

            while (rs.next()) {

                int ignoreID = rs.getInt(IgnoredExpressionTable.IGNORE_ID);
                String exp = rs.getString(IgnoredExpressionTable.IGNORE_STRING);
                boolean mOR = rs.getBoolean(IgnoredExpressionTable.MATCH_OR_CONTAIN);

                IgnoredExpression igEx = new IgnoredExpression(ignoreID, exp, mOR);

                ignoreList.add(igEx);
            }
            return ignoreList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all Ignored Expressions", e);
        }
    }
    @Override
    default boolean checkMatches(String exp) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("SELECT " + IgnoredExpressionTable.IGNORE_ID +
                    " FROM " + IgnoredExpressionTable.TABLE_NAME + " WHERE " +
                IgnoredExpressionTable.IGNORE_STRING+ "=? ");
            stmt.setString(1, exp);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return false;
            }
            return true;
        } catch (java.sql.SQLException e){
            throw new StorageException("Error while comparing a match Ignored Expression");
        }

    }
    @Override
    default boolean checkContains(String exp) throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + IgnoredExpressionTable.IGNORE_STRING +
                    " FROM " + IgnoredExpressionTable.TABLE_NAME + " WHERE " +
                    IgnoredExpressionTable.MATCH_OR_CONTAIN + "= FALSE");
            while(rs.next()){
                String resultExp = rs.getString(IgnoredExpressionTable.IGNORE_STRING);
                if(exp.contains(resultExp)){
                    return false;
                }
            }
            return true;
        } catch (java.sql.SQLException e){
            throw new StorageException("Error while comparing a contains Ignored Expression");
        }
    }
}
