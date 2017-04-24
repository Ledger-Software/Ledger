package ledger.database.storage.SQL;

import ledger.database.entity.RecurringTransaction;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.RecurringTransactionTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database logic relating to {@link RecurringTransaction}.
 */
public interface ISQLDatabaseRecurringTransaction extends ISQLiteDatabase {

    @Override
    default void insertRecurringTransaction(RecurringTransaction trans) throws StorageException {
        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = getDatabase().getAutoCommit();
            setDatabaseAutoCommit(false);

            PreparedStatement stmt = getDatabase().prepareStatement("INSERT INTO " + RecurringTransactionTable.TABLE_NAME +
                    " (" + RecurringTransactionTable.RECURRING_START_DATE +
                    "," + RecurringTransactionTable.RECURRING_END_DATE +
                    "," + RecurringTransactionTable.RECURRING_FREQUENCY +
                    "," + RecurringTransactionTable.RECURRING_AMOUNT +
                    "," + RecurringTransactionTable.RECURRING_ACCOUNT_ID +
                    "," + RecurringTransactionTable.RECURRING_PAYEE_ID +
                    "," + RecurringTransactionTable.RECURRING_TYPE_ID +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?");

            stmt.setLong(1, trans.getStartDate().getTimeInMillis());
            stmt.setLong(2, trans.getEndDate().getTimeInMillis());

            // TODO: Lookup and get Type

        } catch (SQLException e) {
            rollbackDatabase();
            throw new StorageException("Error while adding Recurring Transaction to the database.", e);
        } finally {
            setDatabaseAutoCommit(originalAutoCommit);
        }
    }

    @Override
    default List<RecurringTransaction> getAllRecurringTransactions() {
        List<RecurringTransaction> recurringTransactions = new ArrayList();
        // TODO: Fill the list
        return recurringTransactions;
    }

}
