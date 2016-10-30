package ledger.controller;

import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithArgsReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.H2.H2Database;
import ledger.database.storage.SQL.SQLite.SQLiteDatabase;
import ledger.exception.StorageException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Class for the Database controller
 */
public class DbController {

    public static DbController INSTANCE;
    private IDatabase db;

    static {
        new DbController();
    }

    /**
     * Constructor for the DBcontroller
     */
    public DbController() {
        INSTANCE = this;
        transactionSuccessEvent = new LinkedList<>();
    }

    public void initialize(String fileName, String password) throws StorageException {
        this.db = new H2Database(fileName, password);
    }

    private List<CallableMethodVoidNoArgs> transactionSuccessEvent;

    public void registerTransationSuccessEvent(CallableMethodVoidNoArgs method) {
        transactionSuccessEvent.add(method);
    }

    private void registerSuccess(TaskWithArgs<?> task, List<CallableMethodVoidNoArgs> methods) {
        methods.forEach(task::RegisterSuccessEvent);
    }

    /**
     * @param transaction The transaction to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Transaction> insertTransaction(final Transaction transaction) throws StorageException {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::insertTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;
    }

    /**
     * @param transaction The transaction to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Transaction> deleteTransaction(final Transaction transaction) throws StorageException {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::deleteTransaction, transaction);
        registerSuccess(task,transactionSuccessEvent);
        return task;

    }

    /**
     * <
     *
     * @param transaction The transaction to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Transaction> editTransaction(final Transaction transaction) throws StorageException {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::editTransaction, transaction);
        registerSuccess(task,transactionSuccessEvent);
        return task;
    }

    /**
     * @return A task for the Async Call that returns a list of all the Transactions
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Transaction>> getAllTransactions() throws StorageException {
        return new TaskWithReturn<List<Transaction>>(db::getAllTransactions);

    }

    /**
     * @param account The account to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Account> insertAccount(final Account account) throws StorageException {
        return new TaskWithArgs<Account>(db::insertAccount, account);

    }

    /**
     * @param account The account to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Account> deleteAccount(final Account account) throws StorageException {
        return new TaskWithArgs<Account>(db::deleteAccount, account);

    }

    /**
     * @param account The account to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Account> editAccount(final Account account) throws StorageException {
        return new TaskWithArgs<Account>(db::editAccount, account);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Accounts
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Account>> getAllAccounts() throws StorageException {
        return new TaskWithReturn<List<Account>>(db::getAllAccounts);

    }

    /**
     * @param payee The payee to insert
     * @return a Task for the Async Call
     * @throws StorageException
     */
    public TaskWithArgs<Payee> insertPayee(final Payee payee) throws StorageException {
        return new TaskWithArgs<Payee>(db::insertPayee, payee);

    }

    /**
     * @param payee The payee to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Payee> deletePayee(final Payee payee) throws StorageException {
        return new TaskWithArgs<Payee>(db::deletePayee, payee);

    }

    /**
     * @param payee The payee to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Payee> editPayee(final Payee payee) throws StorageException {
        return new TaskWithArgs<Payee>(db::editPayee, payee);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Payees
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Payee>> getAllPayees() throws StorageException {
        return new TaskWithReturn<List<Payee>>(db::getAllPayees);

    }

    /**
     * @param note The note to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Note> insertNote(final Note note) throws StorageException {
        return new TaskWithArgs<Note>(db::insertNote, note);

    }

    /**
     * @param note The note to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Note> deleteNote(final Note note) throws StorageException {
        return new TaskWithArgs<Note>(db::deleteNote, note);

    }

    /**
     * @param note The note to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Note> editNote(final Note note) throws StorageException {
        return new TaskWithArgs<Note>(db::editNote, note);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Notes
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Note>> getAllNotes() throws StorageException {
        return new TaskWithReturn<List<Note>>(db::getAllNotes);

    }

    /**
     * @param tag the tag to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Tag> insertTag(final Tag tag) throws StorageException {
        return new TaskWithArgs<Tag>(db::insertTag, tag);

    }

    /**
     * @param tag the tag to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Tag> deleteTag(final Tag tag) throws StorageException {
        return new TaskWithArgs<Tag>(db::deleteTag, tag);

    }

    /**
     * @param tag the tag to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Tag> editTag(final Tag tag) throws StorageException {
        return new TaskWithArgs<Tag>(db::editTag, tag);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Transactions
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Tag>> getAllTags() throws StorageException {
        return new TaskWithReturn<List<Tag>>(db::getAllTags);

    }

    /**
     * @param type the type to insert
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Type> insertType(final Type type) throws StorageException {
        return new TaskWithArgs<Type>(db::insertType, type);

    }

    /**
     * @param type the type to delete
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Type> deleteType(final Type type) throws StorageException {
        return new TaskWithArgs<Type>(db::deleteType, type);

    }

    /**
     * @param type the type to edit
     * @return a Task for the Async Call
     * @throws StorageException When a DB error occurs
     */
    public TaskWithArgs<Type> editType(final Type type) throws StorageException {
        return new TaskWithArgs<Type>(db::editType, type);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Types
     * @throws StorageException When a DB error occurs
     */
    public TaskWithReturn<List<Type>> getAllTypes() throws StorageException {
        return new TaskWithReturn<List<Type>>(db::getAllTypes);
    }

    /**
     * Closes the DB safely.
     *
     * @throws StorageException
     */
    public void shutdown() throws StorageException {
        if (db != null)
            db.shutdown();
    }
    public TaskWithArgsReturn<List<Transaction>, List<Transaction>> batchInsertTransaction(List<Transaction> transactions){
        TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = new TaskWithArgsReturn<List<Transaction>, List<Transaction>>((transactionList) ->{
            List<Transaction> list = new ArrayList<>();
            for (Transaction currentTransaction : transactionList) {

                try {
                    db.insertTransaction(currentTransaction);
                } catch (StorageException e) {
                    System.out.println("ERROR - DbContoller.batchInsertTransaction: Adding transaction failed \n" + e.getMessage());
                    list.add(currentTransaction);
                }
            }
            return list;
        }, transactions);
        return task;
    }
    
    protected IDatabase getDb() {
        return db;
    }

}
