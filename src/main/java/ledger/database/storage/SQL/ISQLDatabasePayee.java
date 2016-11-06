package ledger.database.storage.SQL;

import ledger.database.entity.Payee;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.PayeeTable;
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

            PreparedStatement checkIfExistsStmt = getDatabase().prepareStatement("SELECT " + PayeeTable.PAYEE_NAME +
                    " FROM " + PayeeTable.TABLE_NAME + " WHERE " + PayeeTable.PAYEE_NAME + "=?");
            checkIfExistsStmt.setString(1, payee.getName());

            System.out.println(checkIfExistsStmt.toString());

            checkIfExistsStmt.executeQuery();
            ResultSet existingPayees = checkIfExistsStmt.getGeneratedKeys();
            if (existingPayees.next()) {
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

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }

    @Override
    default void deletePayee(Payee payee) throws StorageException {
        try {
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
            PreparedStatement stmt =
                    getDatabase().prepareStatement("UPDATE " + PayeeTable.TABLE_NAME +
                            " SET " + PayeeTable.PAYEE_NAME +
                            " = ?, " + PayeeTable.PAYEE_DESC +
                            " = ? WHERE " + PayeeTable.PAYEE_ID + " = ?");

            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.setInt(3, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Payee", e);
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

                Payee currentPayee = new Payee(payeeName, payeeDesc, payeeID);

                payeeList.add(currentPayee);
            }
            return payeeList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

}
