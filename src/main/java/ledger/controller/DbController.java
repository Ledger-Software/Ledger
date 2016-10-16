package ledger.controller;

import ledger.controller.register.Task;
import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.exception.StorageException;

import java.util.List;


/**
 * Class for the Database conThe one to inserttroller
 */
public class DbController {

    public static DbController INSTANCE;
    //Todo: Who creates this Idatabase?
    private IDatabase db;

    /**
     * Construtior for the DBcontroller
     */
    public DbController() {
        INSTANCE = this;
    }

    /**
     *
     * @param transaction The transaction to insert
     * @return  a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Transaction, Object> insertTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::insertTransaction, transaction);

    }

    /**
     *
     * @param transaction The transaction to delete
     * @return  a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Transaction, Object> deleteTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::deleteTransaction, transaction);

    }

    /**
     *
     * @param transaction The transaction to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Transaction, Object> editTransaction(final Transaction transaction) throws StorageException {
        return new Task<Transaction, Object>(db::editTransaction, transaction);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Transaction>> getAllTransactions() throws StorageException {
        return new Task<Object, List<Transaction>>(db::getAllTransactions);

    }

    /**
     *
     * @param account The account to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Account, Object> insertAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::insertAccount, account);

    }

    /**
     *
     * @param account The account to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Account, Object> deleteAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::deleteAccount, account);

    }

    /**
     *
     * @param account The account to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Account, Object> editAccount(final Account account) throws StorageException {
        return new Task<Account, Object>(db::editAccount, account);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Accounts
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Account>> getAllAccounts() throws StorageException {
        return new Task<Object, List<Account>>(db::getAllAccounts);

    }

    /**
     *
     * @param payee The payee to insert
     * @return a Task for the Async Call
     * @throws StorageException
     */
    public Task<Payee, Object> insertPayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::insertPayee, payee);

    }

    /**
     *
     * @param payee The payee to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Payee, Object> deletePayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::deletePayee, payee);

    }

    /**
     *
     * @param payee The payee to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Payee, Object> editPayee(final Payee payee) throws StorageException {
        return new Task<Payee, Object>(db::editPayee, payee);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Payees
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Payee>> getAllPayees() throws StorageException {
        return new Task<Object, List<Payee>>(db::getAllPayees);

    }

    /**
     *
     * @param note The note to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Note, Object> insertNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::insertNote, note);

    }

    /**
     *
     * @param note The note to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Note, Object> deleteNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::deleteNote, note);

    }

    /**
     *
     * @param note The note to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Note, Object> editNote(final Note note) throws StorageException {
        return new Task<Note, Object>(db::editNote, note);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Notes
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Note>> getAllNotes() throws StorageException {
        return new Task<Object, List<Note>>(db::getAllNotes);

    }

    /**
     *
     * @param tag the tag to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Tag, Object> insertTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::insertTag, tag);

    }

    /**
     *
     * @param tag the tag to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Tag, Object> deleteTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::deleteTag, tag);

    }

    /**
     *
     * @param tag the tag to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Tag, Object> editTag(final Tag tag) throws StorageException {
        return new Task<Tag, Object>(db::editTag, tag);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Tag>> getAllTags() throws StorageException {
        return new Task<Object, List<Tag>>(db::getAllTags);

    }

    /**
     *
     * @param type the type to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Type, Object> insertType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::insertType, type);

    }

    /**
     *
     * @param type the type to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Type, Object> deleteType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::deleteType, type);

    }

    /**
     *
     * @param type the type to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public Task<Type, Object> editType(final Type type) throws StorageException {
        return new Task<Type, Object>(db::editType, type);

    }

    /**
     *
     * @return A task for the Async Call that returns a list of all the Types
     * @throws StorageException When a DB error occurs
     */
    public Task<Object, List<Type>> getAllTypes() throws StorageException {
        return new Task<Object, List<Type>>(db::getAllTypes);

    }

}
