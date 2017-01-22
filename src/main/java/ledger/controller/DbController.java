package ledger.controller;

import ledger.controller.register.CallableMethodVoidNoArgs;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithArgsReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.H2.H2Database;
import ledger.exception.StorageException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Class for the Database controller
 */
public class DbController {

    public static DbController INSTANCE;
    private IDatabase db;
    private File dbFile;

    static {
        new DbController();
    }

    /**
     * Constructor for the DBcontroller
     */
    public DbController() {
        INSTANCE = this;
        transactionSuccessEvent = new LinkedList<>();
        accountSuccessEvent = new LinkedList<>();
    }

    public void initialize(String fileName, String password) throws StorageException {
        this.db = new H2Database(fileName, password);
        this.dbFile = new File(fileName);
    }

    private List<CallableMethodVoidNoArgs> transactionSuccessEvent;
    private List<CallableMethodVoidNoArgs> accountSuccessEvent;

    public void registerTransationSuccessEvent(CallableMethodVoidNoArgs method) {
        transactionSuccessEvent.add(method);
    }

    public void registerAccountSuccessEvent(CallableMethodVoidNoArgs method) {
        accountSuccessEvent.add(method);
    }

    private void registerSuccess(TaskWithArgs<?> task, List<CallableMethodVoidNoArgs> methods) {
        methods.forEach(task::RegisterSuccessEvent);
    }

    /**
     * @param transaction The transaction to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> insertTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::insertTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;
    }

    /**
     * @param transaction The transaction to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> deleteTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::deleteTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;

    }

    /**
     * @param transaction The transaction to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> editTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::editTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;
    }

    /**
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactions() {
        return new TaskWithReturn<List<Transaction>>(db::getAllTransactions);
    }

    /**
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactionsForAccount(Account account) {
        return new TaskWithReturn<List<Transaction>>(() -> db.getAllTransactionsForAccount(account));
    }

    /**
     * @param account The account to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> insertAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::insertAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * @param account The account to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> deleteAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::deleteAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * @param account The account to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> editAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::editAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * @return A task for the Async Call that returns a list of all the Accounts
     */
    public TaskWithReturn<List<Account>> getAllAccounts() {
        return new TaskWithReturn<List<Account>>(db::getAllAccounts);
    }

    /**
     * @param payee The payee to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> insertPayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::insertPayee, payee);

    }

    /**
     * @param payee The payee to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> deletePayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::deletePayee, payee);

    }

    /**
     * @param payee The payee to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> editPayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::editPayee, payee);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Payees
     */
    public TaskWithReturn<List<Payee>> getAllPayees() {
        return new TaskWithReturn<List<Payee>>(db::getAllPayees);

    }

    /**
     * @param note The note to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> insertNote(final Note note) {
        return new TaskWithArgs<Note>(db::insertNote, note);

    }

    /**
     * @param note The note to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> deleteNote(final Note note) {
        return new TaskWithArgs<Note>(db::deleteNote, note);

    }

    /**
     * @param note The note to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> editNote(final Note note) {
        return new TaskWithArgs<Note>(db::editNote, note);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Notes
     */
    public TaskWithReturn<List<Note>> getAllNotes() {
        return new TaskWithReturn<List<Note>>(db::getAllNotes);

    }

    /**
     * @param tag the tag to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> insertTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::insertTag, tag);

    }

    /**
     * @param tag the tag to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> deleteTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::deleteTag, tag);

    }

    /**
     * @param tag the tag to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> editTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::editTag, tag);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Tag>> getAllTags() {
        return new TaskWithReturn<List<Tag>>(db::getAllTags);

    }

    /**
     * @param type the type to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> insertType(final Type type) {
        return new TaskWithArgs<Type>(db::insertType, type);

    }

    /**
     * @param type the type to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> deleteType(final Type type) {
        return new TaskWithArgs<Type>(db::deleteType, type);

    }

    /**
     * @param type the type to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> editType(final Type type) {
        return new TaskWithArgs<Type>(db::editType, type);

    }

    /**
     * @return A task for the Async Call that returns a list of all the Types
     */
    public TaskWithReturn<List<Type>> getAllTypes() {
        return new TaskWithReturn<List<Type>>(db::getAllTypes);
    }


    public TaskWithArgsReturn<Payee, List<Tag>> getTagsForPayee(final Payee payee) {
        return new TaskWithArgsReturn<Payee, List<Tag>>(db::getAllTagsForPayee, payee);
    }

    public TaskWithArgs<Payee> deleteAllTagsForPayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::deleteAllTagsForPayee, payee);
    }

    public TaskWithArgs<TagPayeeWrapper> addTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<TagPayeeWrapper>(this::addTagForPayee, new TagPayeeWrapper(tag, payee));
    }

    public TaskWithArgs<TagPayeeWrapper> deleteTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<TagPayeeWrapper>(this::deleteTagForPayee, new TagPayeeWrapper(tag, payee));
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

    public File getDbFile() {
        return this.dbFile;
    }

    public TaskWithArgsReturn<List<Transaction>, List<Transaction>> batchInsertTransaction(List<Transaction> transactions) {
        TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = new TaskWithArgsReturn<List<Transaction>, List<Transaction>>((transactionList) -> {
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

        for (CallableMethodVoidNoArgs method : transactionSuccessEvent) {
            task.RegisterSuccessEvent((t) -> method.call());
        }

        return task;
    }

    protected IDatabase getDb() {
        return db;
    }


    class TagPayeeWrapper {
        public Tag tag;
        public Payee payee;

        TagPayeeWrapper (Tag t, Payee p) {
            this.tag = t;
            this.payee = p;
        }
    }

    public void addTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.addTagForPayee(wrapper.tag, wrapper.payee);
    }

    public void deleteTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.deleteTagForPayee(wrapper.tag, wrapper.payee);
    }
}
