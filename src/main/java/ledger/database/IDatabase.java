package ledger.database;

import ledger.database.enity.*;
import ledger.exception.StorageException;

/**
 * Responsible for Physical Storage Mechanisms
 */
public interface IDatabase {

    void initializeDatabase() throws StorageException;

//    void openDatabase();

//    void closeDatabase();

//    void wipeDatabase();

    void shutdown() throws StorageException;

    void insertTransaction(Transaction transaction) throws StorageException;

    void deleteTransaction(Transaction transaction) throws StorageException;

    void editTransaction(Transaction transaction) throws StorageException;

    void insertAccount(Account account) throws StorageException;

    void deleteAccount(Account account) throws StorageException;

    void editAccount(Account account) throws StorageException;

    void insertPayee(Payee payee) throws StorageException;

    void deletePayee(Payee payee) throws StorageException;

    void editPayee(Payee payee) throws StorageException;

    void insertType(Type type) throws StorageException;

    void deleteType(Type type) throws StorageException;

    void editType(Type type) throws StorageException;
}
