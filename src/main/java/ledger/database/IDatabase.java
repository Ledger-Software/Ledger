package ledger.database;

import ledger.database.enity.Transaction;

/**
 * Responsible for Physical Storage Mechanisms
 */
public interface IDatabase {

    void initializeDatabase();

    void openDatabase();

    void closeDatabase();

    void wipeDatabase();

    void shutdown();

    void insertTransaction(Transaction transaction);

    void deleteTransaction(Transaction transaction);

    void editTransaction(Transaction transaction);

    void insertAccount(Account account);

    void deleteAccount(Account account);

    void editAccount(Account account);

    void insertPayee(Payee payee);

    void deletePayee(Payee payee);

    void editPayee(Payee payee);

    void insertType(Type type);

    void deleteType(Type type);

    void editType(Type type);
}
