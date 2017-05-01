package ledger.database.storage.SQL;

import ledger.database.entity.*;
import ledger.database.storage.table.RecurringTransactionTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handles all database logic relating to {@link RecurringTransaction}.
 */
public interface ISQLDatabaseRecurringTransaction extends ISQLDatabaseTransaction {

    @Override
    default void insertRecurringTransaction(RecurringTransaction trans) throws StorageException {
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = getDatabase().getAutoCommit();
            setDatabaseAutoCommit(false);

            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + RecurringTransactionTable.TABLE_NAME +
                    " (" + RecurringTransactionTable.RECURRING_START_DATE +
                    "," + RecurringTransactionTable.RECURRING_END_DATE +
                    "," + RecurringTransactionTable.RECURRING_NEXT_TRIGGER_DATE +
                    "," + RecurringTransactionTable.RECURRING_FREQUENCY_ID +
                    "," + RecurringTransactionTable.RECURRING_AMOUNT +
                    "," + RecurringTransactionTable.RECURRING_ACCOUNT_ID +
                    "," + RecurringTransactionTable.RECURRING_PAYEE_ID +
                    "," + RecurringTransactionTable.RECURRING_TYPE_ID +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            stmt.setLong(1, trans.getStartDate().getTimeInMillis());
            stmt.setLong(2, trans.getEndDate().getTimeInMillis());
            stmt.setLong(3, trans.getNextTriggerDate().getTimeInMillis());
            stmt.setInt(4, getIdForFrequency(trans.getFrequency()));
            stmt.setLong(5, trans.getAmount());
            lookupAndSetAccountForSQLStatement(trans, stmt, 6);
            lookupAndSetPayeeForSQLStatement(trans, stmt, 7);
            lookupAndSetTypeForSQLStatement(trans, stmt, 8);

            stmt.executeUpdate();
        } catch (SQLException e) {
            rollbackDatabase();
            throw new StorageException("Error while adding Recurring Transaction to the database.", e);
        } finally {
            setDatabaseAutoCommit(originalAutoCommit);
        }
    }

    @Override
    default void deleteRecurringTransaction(RecurringTransaction recurringTransaction) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + RecurringTransactionTable.TABLE_NAME +
                    " WHERE " + RecurringTransactionTable.RECURRING_ID + " = ?;");
            stmt.setInt(1, recurringTransaction.getId());
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Recurring Transaction from the database.", e);
        }
    }

    @Override
    default List<RecurringTransaction> getAllRecurringTransactions() throws StorageException {
        List<RecurringTransaction> recurringTransactions = new ArrayList();
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + RecurringTransactionTable.RECURRING_ID +
                    ", " + RecurringTransactionTable.RECURRING_START_DATE +
                    ", " + RecurringTransactionTable.RECURRING_END_DATE +
                    ", " + RecurringTransactionTable.RECURRING_NEXT_TRIGGER_DATE +
                    "," + RecurringTransactionTable.RECURRING_FREQUENCY_ID +
                    "," + RecurringTransactionTable.RECURRING_AMOUNT +
                    "," + RecurringTransactionTable.RECURRING_ACCOUNT_ID +
                    "," + RecurringTransactionTable.RECURRING_PAYEE_ID +
                    "," + RecurringTransactionTable.RECURRING_TYPE_ID +
                    " FROM " + RecurringTransactionTable.TABLE_NAME +
                    ";");

            while (rs.next()) {
                int recurringId = rs.getInt(RecurringTransactionTable.RECURRING_ID);

                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(rs.getLong(RecurringTransactionTable.RECURRING_START_DATE));

                Calendar endDate = Calendar.getInstance();
                endDate.setTimeInMillis(rs.getLong(RecurringTransactionTable.RECURRING_END_DATE));

                Calendar nextTriggerDate = Calendar.getInstance();
                nextTriggerDate.setTimeInMillis(rs.getLong(RecurringTransactionTable.RECURRING_NEXT_TRIGGER_DATE));

                int frequencyId = rs.getInt(RecurringTransactionTable.RECURRING_FREQUENCY_ID);
                Frequency freq = getFrequencyById(frequencyId);

                long amount = rs.getLong(RecurringTransactionTable.RECURRING_AMOUNT);

                int accountId = rs.getInt(RecurringTransactionTable.RECURRING_ACCOUNT_ID);
                Account recurringAccount = getAccountForID(accountId);

                int payeeId = rs.getInt(RecurringTransactionTable.RECURRING_PAYEE_ID);
                Payee payee = getPayeeForID(payeeId);

                int typeId = rs.getInt(RecurringTransactionTable.RECURRING_TYPE_ID);
                Type type = getTypeForID(typeId);

                RecurringTransaction rt = new RecurringTransaction(startDate, endDate, nextTriggerDate, type, amount,
                        recurringAccount, payee, null, null, freq, recurringId);

                recurringTransactions.add(rt);
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Unable to retrieve all Recurring Transactions from database.", e);
        }
        return recurringTransactions;
    }

    @Override
    default void editRecurringTransaction(RecurringTransaction recurringTransaction) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("UPDATE " + RecurringTransactionTable.TABLE_NAME + " SET " +
                    RecurringTransactionTable.RECURRING_START_DATE + "=?," +
                    RecurringTransactionTable.RECURRING_END_DATE + "=?," +
                    RecurringTransactionTable.RECURRING_NEXT_TRIGGER_DATE + "=?," +
                    RecurringTransactionTable.RECURRING_FREQUENCY_ID + "=?," +
                    RecurringTransactionTable.RECURRING_AMOUNT + "=?," +
                    RecurringTransactionTable.RECURRING_ACCOUNT_ID + "=?," +
                    RecurringTransactionTable.RECURRING_PAYEE_ID + "=?," +
                    RecurringTransactionTable.RECURRING_TYPE_ID + "=?" + " WHERE " +
                    RecurringTransactionTable.RECURRING_ID + "=?");

            stmt.setLong(1, recurringTransaction.getStartDate().getTimeInMillis());
            stmt.setLong(2, recurringTransaction.getEndDate().getTimeInMillis());
            stmt.setLong(3, recurringTransaction.getNextTriggerDate().getTimeInMillis());
            stmt.setInt(4, getIdForFrequency(recurringTransaction.getFrequency()));
            stmt.setLong(5, recurringTransaction.getAmount());
            lookupAndSetAccountForSQLStatement(recurringTransaction, stmt, 6);
            lookupAndSetPayeeForSQLStatement(recurringTransaction, stmt, 7);
            lookupAndSetTypeForSQLStatement(recurringTransaction, stmt, 8);
            stmt.setInt(9, recurringTransaction.getId());

            stmt.executeUpdate();
        } catch (java.sql.SQLException e){
            throw new StorageException("Error while editing Recurring Transaction.", e);
        }
    }
}
