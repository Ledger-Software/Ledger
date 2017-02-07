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
        payeeSuccessEvent = new LinkedList<>();
        ignoredExpSuccessEvent = new LinkedList<>();
    }

    public void initialize(String fileName, String password) throws StorageException {
        this.db = new H2Database(fileName, password);
        this.dbFile = new File(fileName);
    }

    private List<CallableMethodVoidNoArgs> transactionSuccessEvent;
    private List<CallableMethodVoidNoArgs> accountSuccessEvent;
    private List<CallableMethodVoidNoArgs> payeeSuccessEvent;
    private List<CallableMethodVoidNoArgs> ignoredExpSuccessEvent;

    public void registerTransationSuccessEvent(CallableMethodVoidNoArgs method) {
        transactionSuccessEvent.add(method);
    }

    public void registerAccountSuccessEvent(CallableMethodVoidNoArgs method) {
        accountSuccessEvent.add(method);
    }

    public void registerPayyeeSuccessEvent(CallableMethodVoidNoArgs method) { payeeSuccessEvent.add(method); }

    public void registerIgnoredExpressionSuccessEvent(CallableMethodVoidNoArgs method) { ignoredExpSuccessEvent.add(method); }

    private void registerSuccess(TaskWithArgs<?> task, List<CallableMethodVoidNoArgs> methods) {
        methods.forEach(task::RegisterSuccessEvent);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert a Transaction
     *
     * @param transaction The transaction to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> insertTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::insertTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete a Transaction
     *
     * @param transaction The transaction to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> deleteTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::deleteTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit a Transaction
     *
     * @param transaction The transaction to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Transaction> editTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<Transaction>(db::editTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all stored Transactions
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactions() {
        return new TaskWithReturn<List<Transaction>>(db::getAllTransactions);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all transactions
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
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> insertAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::insertAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete an Account
     *
     * @param account The account to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> deleteAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::deleteAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit an Account
     *
     * @param account The account to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Account> editAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<Account>(db::editAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all Accounts
     *
     * @return A task for the Async Call that returns a list of all the Accounts
     */
    public TaskWithReturn<List<Account>> getAllAccounts() {
        return new TaskWithReturn<List<Account>>(db::getAllAccounts);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert a Payee
     *
     * @param payee The payee to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> insertPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::insertPayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete a Payee
     *
     * @param payee The payee to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> deletePayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::deletePayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit a Payee
     *
     * @param payee The payee to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Payee> editPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<Payee>(db::editPayee, payee);
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all stored Payees
     *
     * @return A task for the Async Call that returns a list of all the Payees
     */
    public TaskWithReturn<List<Payee>> getAllPayees() {
        return new TaskWithReturn<List<Payee>>(db::getAllPayees);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert a Note
     *
     * @param note The note to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> insertNote(final Note note) {
        return new TaskWithArgs<Note>(db::insertNote, note);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete a Note
     *
     * @param note The note to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> deleteNote(final Note note) {
        return new TaskWithArgs<Note>(db::deleteNote, note);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit a Note
     *
     * @param note The note to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Note> editNote(final Note note) {
        return new TaskWithArgs<Note>(db::editNote, note);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all Notes
     *
     * @return A task for the Async Call that returns a list of all the Notes
     */
    public TaskWithReturn<List<Note>> getAllNotes() {
        return new TaskWithReturn<List<Note>>(db::getAllNotes);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert a Tag
     *
     * @param tag the tag to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> insertTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::insertTag, tag);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete a Tag
     *
     * @param tag the tag to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> deleteTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::deleteTag, tag);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit a Tag
     *
     * @param tag the tag to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Tag> editTag(final Tag tag) {
        return new TaskWithArgs<Tag>(db::editTag, tag);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all Tags
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Tag>> getAllTags() {
        return new TaskWithReturn<List<Tag>>(db::getAllTags);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert a Type
     *
     * @param type the type to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> insertType(final Type type) {
        return new TaskWithArgs<Type>(db::insertType, type);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete a Type
     *
     * @param type the type to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> deleteType(final Type type) {
        return new TaskWithArgs<Type>(db::deleteType, type);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit a Type
     *
     * @param type the type to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<Type> editType(final Type type) {
        return new TaskWithArgs<Type>(db::editType, type);

    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all Types
     *
     * @return A task for the Async Call that returns a list of all the Types
     */
    public TaskWithReturn<List<Type>> getAllTypes() {
        return new TaskWithReturn<List<Type>>(db::getAllTypes);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A task for the Async call that returns a list of all the Tags associated with the Payee
     */
    public TaskWithArgsReturn<Payee, List<Tag>> getTagsForPayee(final Payee payee) {
        return new TaskWithArgsReturn<Payee, List<Tag>>(db::getAllTagsForPayee, payee);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A Task for the Async call
     */
    public TaskWithArgs<Payee> deleteAllTagsForPayee(final Payee payee) {
        return new TaskWithArgs<Payee>(db::deleteAllTagsForPayee, payee);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database add a Tag to the given Payee
     *
     * @param payee The Payee to get all associated tags for
     * @param tag The Tag to associate with the Payee
     * @return A Task for the async call
     */
    public TaskWithArgs<TagPayeeWrapper> addTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<TagPayeeWrapper>(this::addTagForPayee, new TagPayeeWrapper(tag, payee));
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to remove a Tag from the given
     * Payee
     *
     * @param payee The Payee to remove the Tag from
     * @param tag The Tag to remove from the Payee
     * @return A Task for the asynchronous call
     */
    public TaskWithArgs<TagPayeeWrapper> deleteTagForPayee(final Payee payee, final Tag tag) {
        return new TaskWithArgs<TagPayeeWrapper>(this::deleteTagForPayee, new TagPayeeWrapper(tag, payee));
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
     * Creates a Task that can be used to make an asynchronous call to the database to add transactions as a batch
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
     * Creates a Task that can be used to make an asynchronous call to the database to add transactions as a batch with Ignore checks
     *
     * @param transactions List of transactions to add to the database
     * @return A task for the asynchronous call
     */
    public TaskWithArgsReturn<List<Transaction>, List<Transaction>> batchInsertTransactionIgnoreCheck(List<Transaction> transactions) {
        TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = new TaskWithArgsReturn<List<Transaction>, List<Transaction>>((transactionList) -> {
            List<Transaction> list = new ArrayList<>();
            for (Transaction currentTransaction : transactionList) {

                try {
                    db.insertTransactionWithIgnoreCheck(currentTransaction);
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

    /**
     * Wraps a Tag and Payee into a single class to be used by Tasks
     */
    class TagPayeeWrapper {
        public Tag tag;
        public Payee payee;

        TagPayeeWrapper (Tag t, Payee p) {
            this.tag = t;
            this.payee = p;
        }
    }

    /**
     * Wraps the databases addTagForPayee method so that a single argument can be given to the Task
     *
     * @param wrapper The wrapper class containing the Tag and Payee
     * @throws StorageException When an SQLExeption occurs
     */
    public void addTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.addTagForPayee(wrapper.tag, wrapper.payee);
    }

    /**
     * Wraps the databases deleteTagForPayee method so that a single argument can be given to the Task
     *
     * @param wrapper The wrapper class containing the Tag and Payee
     * @throws StorageException When an SQLExeption occurs
     */
    public void deleteTagForPayee(TagPayeeWrapper wrapper) throws StorageException {
        db.deleteTagForPayee(wrapper.tag, wrapper.payee);
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to insert an Ignored Expression
     *
     * @param igEx the IgnoredExpression to insert
     * @return a Task for the Async Call
     */
    public TaskWithArgs<IgnoredExpression> insertIgnoredExpression(IgnoredExpression igEx){
        TaskWithArgs task =  new TaskWithArgs<>(db::insertIgnoredExpression,igEx);
        registerSuccess(task, ignoredExpSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit an Ignored Expression
     *
     * @param igEx the IgnoredExpression to edit
     * @return a Task for the Async Call
     */
    public TaskWithArgs<IgnoredExpression> editIgnoredExpression(IgnoredExpression igEx){
        TaskWithArgs task =  new TaskWithArgs<>(db::editIgnoredExpression,igEx);
        registerSuccess(task, ignoredExpSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete an Ignored Expression
     *
     * @param igEx the IgnoredExpression to delete
     * @return a Task for the Async Call
     */
    public TaskWithArgs<IgnoredExpression> deleteIgnoredExpression(IgnoredExpression igEx){
        TaskWithArgs task =  new TaskWithArgs<>(db::deleteIgnoredExpression,igEx);
        registerSuccess(task, ignoredExpSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to get all IgnoredExpressions
     *
     * @return A task for the Async Call that returns a list of all the IgnoredExpressions
     */
    public TaskWithReturn<List<IgnoredExpression>> getAllIgnoredExpressions() {
        return new TaskWithReturn<List<IgnoredExpression>>(db::getAllIgnoredExpressions);
    }


}
