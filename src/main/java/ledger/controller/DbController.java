package ledger.controller;

import ledger.controller.register.*;
import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.H2.H2Database;
import ledger.exception.StorageException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * Class for the Database controller
 */
public class DbController {

    public static DbController INSTANCE;

    static {
        new DbController();
    }

    private IDatabase db;
    private File dbFile;
    private List<CallableMethodVoidNoArgs> transactionSuccessEvent;
    private List<CallableMethodVoidNoArgs> accountSuccessEvent;
    private List<CallableMethodVoidNoArgs> payeeSuccessEvent;

    private Stack<UndoAction> undoStack;

    /**
     * Constructor for the DBcontroller
     */
    public DbController() {
        INSTANCE = this;
        transactionSuccessEvent = new LinkedList<>();
        accountSuccessEvent = new LinkedList<>();
        payeeSuccessEvent = new LinkedList<>();
        undoStack = new Stack<UndoAction>();
    }

    public void initialize(String fileName, String password) throws StorageException {
        this.db = new H2Database(fileName, password);
        this.dbFile = new File(fileName);
    }

    public void registerTransationSuccessEvent(CallableMethodVoidNoArgs method) {
        transactionSuccessEvent.add(method);
    }

    public void registerAccountSuccessEvent(CallableMethodVoidNoArgs method) {
        accountSuccessEvent.add(method);
    }

    public void registerPayyeeSuccessEvent(CallableMethodVoidNoArgs method) {
        payeeSuccessEvent.add(method);
    }

    private void registerSuccess(TaskWithArgs<?> task, List<CallableMethodVoidNoArgs> methods) {
        methods.forEach(task::RegisterSuccessEvent);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Transaction
     *
     * @param transaction The transaction to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Transaction> insertTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::insertTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);

        undoStack.push(new UndoAction(deleteTransaction(transaction), "Undo Insert Transaction", null));
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Transaction
     *
     * @param transaction The transaction to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Transaction> deleteTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::deleteTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);

        undoStack.push(new UndoAction(insertTransaction(transaction), "Undo Delete Transaction", null));
        return task;

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Transaction
     *
     * @param transaction The transaction to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Transaction> editTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::editTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all stored Transactions
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactions() {
        return new TaskWithReturn<List<Transaction>>(db::getAllTransactions);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all transactions
     * associated with the given account
     *
     * @param account The account to retrieve all transactions assioted with
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactionsForAccount(Account account) {
        return new TaskWithReturn<List<Transaction>>(() -> db.getAllTransactionsForAccount(account));
    }

    /**
     * @param account The account to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Account> insertAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::insertAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete an Account
     *
     * @param account The account to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Account> deleteAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::deleteAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit an Account
     *
     * @param account The account to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Account> editAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::editAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Accounts
     *
     * @return A task for the Async Call that returns a list of all the Accounts
     */
    public TaskWithReturn<List<Account>> getAllAccounts() {
        return new TaskWithReturn<List<Account>>(db::getAllAccounts);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Payee
     *
     * @param payee The payee to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Payee> insertPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::insertPayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Payee
     *
     * @param payee The payee to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Payee> deletePayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::deletePayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Payee
     *
     * @param payee The payee to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Payee> editPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::editPayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all stored Payees
     *
     * @return A task for the Async Call that returns a list of all the Payees
     */
    public TaskWithReturn<List<Payee>> getAllPayees() {
        return new TaskWithReturn<List<Payee>>(db::getAllPayees);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Note
     *
     * @param note The note to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Note> insertNote(final Note note) {
        return new TaskWithArgs<Note>(db::insertNote, note);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Note
     *
     * @param note The note to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Note> deleteNote(final Note note) {
        return new TaskWithArgs<Note>(db::deleteNote, note);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Note
     *
     * @param note The note to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Note> editNote(final Note note) {
        return new TaskWithArgs<Note>(db::editNote, note);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Notes
     *
     * @return A task for the Async Call that returns a list of all the Notes
     */
    public TaskWithReturn<List<Note>> getAllNotes() {
        return new TaskWithReturn<List<Note>>(db::getAllNotes);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Tag
     *
     * @param tag the tag to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Tag> insertTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::insertTag, tag);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Tag
     *
     * @param tag the tag to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Tag> deleteTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::deleteTag, tag);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Tag
     *
     * @param tag the tag to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Tag> editTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::editTag, tag);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Tags
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Tag>> getAllTags() {
        return new TaskWithReturn<List<Tag>>(db::getAllTags);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Type
     *
     * @param type the type to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Type> insertType(final Type type) {
        return new TaskWithArgs<Type>(db::insertType, type);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Type
     *
     * @param type the type to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Type> deleteType(final Type type) {
        return new TaskWithArgs<Type>(db::deleteType, type);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Type
     *
     * @param type the type to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Type> editType(final Type type) {
        return new TaskWithArgs<Type>(db::editType, type);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Types
     *
     * @return A task for the Async Call that returns a list of all the Types
     */
    public TaskWithReturn<List<Type>> getAllTypes() {
        return new TaskWithReturn<List<Type>>(db::getAllTypes);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A task for the Async call that returns a list of all the Tags associated with the Payee
     */
    public TaskWithArgsReturn<Payee, List<Tag>> getTagsForPayee(final Payee payee) {
        return new TaskWithArgsReturn<Payee, List<Tag>>(db::getAllTagsForPayee, payee);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A ITask for the Async call
     */
    public TaskWithArgs<Payee> deleteAllTagsForPayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::deleteAllTagsForPayee, payee);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database add a Tag to the given Payee
     *
     * @param payee The Payee to get all associated tags for
     * @param tag   The Tag to associate with the Payee
     * @return A ITask for the async call
     */
    public TaskWithArgs<TagPayeeWrapper> addTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<TagPayeeWrapper>(this::addTagForPayee, new TagPayeeWrapper(tag, payee));
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to remove a Tag from the given
     * Payee
     *
     * @param payee The Payee to remove the Tag from
     * @param tag   The Tag to remove from the Payee
     * @return A ITask for the asynchronous call
     */
    public TaskWithArgs<TagPayeeWrapper> deleteTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<>(this::deleteTagForPayee, new TagPayeeWrapper(tag, payee));
    }


    /**
     * Closes the DB safely.
     *
     * @throws StorageException When DB could not be closed successfully
     */
    public void shutdown() throws StorageException {
        if (db != null)
            db.shutdown();
    }

    public File getDbFile() {
        return this.dbFile;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to add transactions as a batch
     *
     * @param transactions List of transactions to add to the database
     * @return A task for the asynchronous call
     */
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

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get the balance for a given account
     *
     * @param account The account to get the AccountBalance for
     * @return A task for the asynchronous call
     */
    public TaskWithArgsReturn<Account, AccountBalance> getBalanceForAccount(Account account) {
        return new TaskWithArgsReturn<>(db::getBalanceForAccount, account);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to add an account balance for a given
     * account
     *
     * @param balance AccountBalance to add to the database
     * @return A task for the asynchronous call
     */
    public TaskWithArgs<AccountBalance> addBalanceForAccount(AccountBalance balance) {
        return new TaskWithArgs<>(db::addBalanceForAccount, balance);
    }

    protected IDatabase getDb() {
        return db;
    }

    /**
     * Wraps the databases addTagForPayee method so that a single argument can be given to the ITask
     *
     * @param wrapper The wrapper class containing the Tag and Payee
     * @throws StorageException When an SQLExeption occurs
     */
    public void addTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.addTagForPayee(wrapper.tag, wrapper.payee);
    }

    /**
     * Wraps the databases deleteTagForPayee method so that a single argument can be given to the ITask
     *
     * @param wrapper The wrapper class containing the Tag and Payee
     * @throws StorageException When an SQLExeption occurs
     */
    public void deleteTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.deleteTagForPayee(wrapper.tag, wrapper.payee);
    }

    /**
     * Wraps a Tag and Payee into a single class to be used by Tasks
     */
    class TagPayeeWrapper {
        public Tag tag;
        public Payee payee;

        TagPayeeWrapper(Tag t, Payee p) {
            this.tag = t;
            this.payee = p;
        }
    }

}
