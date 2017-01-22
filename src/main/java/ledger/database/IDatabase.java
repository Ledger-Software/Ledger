package ledger.database;

import ledger.database.entity.*;
import ledger.exception.StorageException;

import java.util.List;

/**
 * Responsible for Physical Storage Mechanisms
 */
public interface IDatabase {

    // DB management functions
    void initializeDatabase() throws StorageException;

    void shutdown() throws StorageException;

    // Basic CRUD functions
    void insertTransaction(Transaction transaction) throws StorageException;

    void deleteTransaction(Transaction transaction) throws StorageException;

    void editTransaction(Transaction transaction) throws StorageException;

    List<Transaction> getAllTransactions() throws StorageException;

    List<Transaction> getAllTransactionsForAccount(Account account) throws StorageException;

    void insertAccount(Account account) throws StorageException;

    void deleteAccount(Account account) throws StorageException;

    void editAccount(Account account) throws StorageException;

    List<Account> getAllAccounts() throws StorageException;

    void insertPayee(Payee payee) throws StorageException;

    void deletePayee(Payee payee) throws StorageException;

    void editPayee(Payee payee) throws StorageException;

    List<Payee> getAllPayees() throws StorageException;

    void insertNote(Note note) throws StorageException;

    void deleteNote(Note note) throws StorageException;

    void editNote(Note note) throws StorageException;

    List<Note> getAllNotes() throws StorageException;

    void insertType(Type type) throws StorageException;

    void deleteType(Type type) throws StorageException;

    void editType(Type type) throws StorageException;

    List<Type> getAllTypes() throws StorageException;

    void insertTag(Tag tag) throws StorageException;

    void deleteTag(Tag tag) throws StorageException;

    void editTag(Tag tag) throws StorageException;

    List<Tag> getAllTags() throws StorageException;

    List<Tag> getAllTagsForPayee(Payee payee) throws StorageException;

    void deleteTagForPayee(Tag tag, Payee payee) throws StorageException;

    void deleteAllTagsForPayee(Payee payee) throws StorageException;

    void addTagForPayee(Tag tag, Payee payee) throws StorageException;

    Payee getPayeeForNameAndDescription(String payeeName, String payeeDescription);

    Tag getTagForNameAndDescription(String tagName, String tagDescription);
}

