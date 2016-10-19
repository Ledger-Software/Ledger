package ledger.database.storage;

import ledger.database.enity.Account;
import ledger.database.storage.table.AccountTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLiteDatabaseAccount extends ISQLiteDatabase {

    @Override
    default void insertAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + AccountTable.TABLE_NAME +
                    " (" + AccountTable.ACCOUNT_NAME + "," + AccountTable.ACCOUNT_DESC + ") VALUES (?, ?)");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedAccountID = generatedIDs.getInt(1);
                account.setId(insertedAccountID);
            }

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Account", e);
        }
    }

    @Override
    default void deleteAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + AccountTable.TABLE_NAME +
                    " WHERE " + AccountTable.ACCOUNT_ID + " = ?");
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Account", e);
        }
    }

    @Override
    default void editAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE " + AccountTable.TABLE_NAME +
                    " SET " + AccountTable.ACCOUNT_NAME + "=?, " + AccountTable.ACCOUNT_DESC +
                    "=? WHERE " + AccountTable.ACCOUNT_ID + "=?");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.setInt(3, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Account", e);
        }
    }

    @Override
    default List<Account> getAllAccounts() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + AccountTable.ACCOUNT_ID +
                    ", " + AccountTable.ACCOUNT_NAME +
                    ", " + AccountTable.ACCOUNT_DESC +
                    " FROM " + AccountTable.TABLE_NAME + ";");

            List<Account> accountList = new ArrayList<>();

            while (rs.next()) {

                int accountID = rs.getInt(AccountTable.ACCOUNT_ID);
                String accountName = rs.getString(AccountTable.ACCOUNT_NAME);
                String accountDesc = rs.getString(AccountTable.ACCOUNT_DESC);

                Account currentAccount = new Account(accountName, accountDesc, accountID);
                accountList.add(currentAccount);
            }

            return accountList;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all payees", e);
        }
    }

}
