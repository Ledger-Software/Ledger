package ledger.controller;

import com.google.api.client.util.Lists;
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
    private List<CallableMethodVoidNoArgs> ignoredExpSuccessEvent;

    private Stack<UndoAction> undoStack;

    /**
     * Constructor for the DBcontroller
     */
    private DbController() {
        INSTANCE = this;
        transactionSuccessEvent = new LinkedList<>();
        accountSuccessEvent = new LinkedList<>();
        payeeSuccessEvent = new LinkedList<>();
        ignoredExpSuccessEvent = new LinkedList<>();
        undoStack = new Stack<>();
    }

    public void initialize(String fileName, String password) throws StorageException {
        this.db = new H2Database(fileName, password);
        this.dbFile = new File(fileName);
        undoStack.clear();
    }

    public void registerTransactionSuccessEvent(CallableMethodVoidNoArgs method) {
        transactionSuccessEvent.add(method);
    }

    public void registerAccountSuccessEvent(CallableMethodVoidNoArgs method) {
        accountSuccessEvent.add(method);
    }

    public void registerPayeeSuccessEvent(CallableMethodVoidNoArgs method) {
        payeeSuccessEvent.add(method);
    }

    public void registerIgnoredExpressionSuccessEvent(CallableMethodVoidNoArgs method) { ignoredExpSuccessEvent.add(method); }

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
        TaskWithArgs<Transaction> task = generateInsertTransaction(transaction);

        undoStack.push(new UndoAction(generateDeleteTransaction(transaction), "Undo Insert Transaction"));
        return task;
    }

    private TaskWithArgs<Transaction> generateInsertTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<>(db::insertTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        registerSuccess(task, payeeSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Transaction
     *
     * @param transaction The transaction to delete
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Transaction> deleteTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = generateDeleteTransaction(transaction);

        undoStack.push(new UndoAction(generateInsertTransaction(transaction), "Undo Delete Transaction"));
        return task;

    }

    private TaskWithArgs<Transaction> generateDeleteTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<>(db::deleteTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);

        return task;

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Transaction
     *
     * @param transaction The transaction to edit
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Transaction> editTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = generateEditTransaction(transaction);

        Transaction oldTrans = null;
        try {
            oldTrans = db.getTransactionById(transaction);
        } catch (StorageException e) {
            System.err.println("Error on getTransactionById");
        }

        undoStack.push(new UndoAction(generateEditTransaction(oldTrans),"Undo Edit Transaction"));

        return task;
    }

    private TaskWithArgs<Transaction> generateEditTransaction(final Transaction transaction) {
        TaskWithArgs<Transaction> task = new TaskWithArgs<>(db::editTransaction, transaction);
        registerSuccess(task, transactionSuccessEvent);
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all stored Transactions
     *
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactions() {
        return new TaskWithReturn<>(db::getAllTransactions);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all transactions
     * associated with the given account
     *
     * @param account The account to retrieve all transactions associated with
     * @return A task for the Async Call that returns a list of all the Transactions
     */
    public TaskWithReturn<List<Transaction>> getAllTransactionsForAccount(Account account) {
        return new TaskWithReturn<>(() -> db.getAllTransactionsForAccount(account));
    }

    /**
     * @param account The account to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Account> insertAccount(final Account account) {
        TaskWithArgs<Account> task = generateInsertAccount(account);

        undoStack.push(new UndoAction(generateDeleteAccount(account), "Undo Insert Account"));

        return task;
    }

    private TaskWithArgs<Account> generateInsertAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<>(db::insertAccount, account);
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
        TaskWithArgs<Account> task = generateDeleteAccount(account);

        AccountBalance ab = null;
        try {
            ab = db.getBalanceForAccount(account);
        } catch (StorageException ignored) { }

        List<Transaction> trans;
        try {
            trans = db.getAllTransactionsForAccount(account);
        } catch (StorageException e) {
            trans = new ArrayList<>();
        }

        if(trans.size() != 0) {
            final AccountBalance finalAb = ab;
            TaskWithArgs<List<Transaction>> undoTask = new TaskWithArgs<>((toAddTrans) -> {
                db.insertAccount(account);
                for (Transaction t : toAddTrans) {
                    t.setAccount(account);
                    db.insertTransaction(t);
                }
                finalAb.setAccount(account);
                db.addBalanceForAccount(finalAb);
            }, trans);
            registerSuccess(undoTask, transactionSuccessEvent);
            registerSuccess(undoTask, accountSuccessEvent);
            undoStack.push(new UndoAction(undoTask, "Undo Delete Account and Transactions"));
        } else {
            TaskWithArgs<AccountBalance> undoTask = new TaskWithArgs<>((finalAb) -> {
                db.insertAccount(account);
                finalAb.setAccount(account);
                db.addBalanceForAccount(finalAb);
            }, ab);
            registerSuccess(undoTask, transactionSuccessEvent);
            registerSuccess(undoTask, accountSuccessEvent);
            undoStack.push(new UndoAction(undoTask, "Undo Delete Account"));
        }

        return task;
    }

    private TaskWithArgs<Account> generateDeleteAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<>(db::deleteAccount, account);
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
        TaskWithArgs<Account> task = generateEditAccount(account);

        Account oldAccount = null;
        try {
            oldAccount = db.getAccountById(account);
        } catch (StorageException ignored) { }

        undoStack.push(new UndoAction(generateEditAccount(oldAccount),"Undo Edit Transaction"));

        return task;
    }

    private TaskWithArgs<Account> generateEditAccount(final Account account) {
        TaskWithArgs<Account> task = new TaskWithArgs<>(db::editAccount, account);
        registerSuccess(task, accountSuccessEvent);
        return task;
    }


    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Accounts
     *
     * @return A task for the Async Call that returns a list of all the Accounts
     */
    public TaskWithReturn<List<Account>> getAllAccounts() {
        return new TaskWithReturn<>(db::getAllAccounts);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Payee
     *
     * @param payee The payee to insert
     * @return a ITask for the Async Call
     */
    public TaskWithArgs<Payee> insertPayee(final Payee payee) {
        TaskWithArgs<Payee> task = generateInsertPayee(payee);
        undoStack.push(new UndoAction(generateDeletePayee(payee), "Undo Insert Payee"));
        return task;

    }

    private TaskWithArgs<Payee> generateInsertPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<>(db::insertPayee, payee);
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
        TaskWithArgs<Payee> task = generateDeletePayee(payee);
        undoStack.push(new UndoAction(generateInsertPayee(payee), "Undo Delete Payee"));
        return task;
    }

    private TaskWithArgs<Payee> generateDeletePayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<>(db::deletePayee, payee);
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
        TaskWithArgs<Payee> task = generateEditPayee(payee);

        Payee oldPayee = null;
        try {
            oldPayee = db.getPayeeById(payee);
        } catch (StorageException ignored) { }

        undoStack.push(new UndoAction(generateEditPayee(oldPayee), "Undo Edit Payee"));

        return task;
    }

    private TaskWithArgs<Payee> generateEditPayee(final Payee payee) {
        TaskWithArgs<Payee> task = new TaskWithArgs<>(db::editPayee, payee);
        registerSuccess(task, payeeSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all stored Payees
     *
     * @return A task for the Async Call that returns a list of all the Payees
     */
    public TaskWithReturn<List<Payee>> getAllPayees() {
        return new TaskWithReturn<>(db::getAllPayees);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Types
     *
     * @return A task for the Async Call that returns a list of all the Types
     */
    public TaskWithReturn<List<Type>> getAllTypes() {
        return new TaskWithReturn<>(db::getAllTypes);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A task for the Async call that returns a list of all the Tags associated with the Payee
     */
    public TaskWithArgsReturn<Payee, List<Tag>> getTagsForPayee(final Payee payee) {
        return new TaskWithArgsReturn<>(db::getAllTagsForPayee, payee);
    }

    /**
     * Closes the DB safely.
     *
     * @throws StorageException When DB could not be closed successfully
     */
    public void shutdown() throws StorageException {
        if (db != null)
            db.shutdown();
        undoStack.clear();
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
        TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = generateBatchInsertTransaction(transactions);

        List<Transaction> copyList = Lists.newArrayList(transactions);

        TaskWithArgs<List<Transaction>> undoTask = new TaskWithArgs<>((transactionList) -> {
            try {
                db.setDatabaseAutoCommit(false);

                for (Transaction currentTransaction : transactionList) {
                    try {
                        db.deleteTransaction(currentTransaction);
                    } catch (StorageException ignored) { }
                }
            } finally {
                db.setDatabaseAutoCommit(true);
            }
        }, copyList);
        registerSuccess(undoTask, transactionSuccessEvent);

        undoStack.push(new UndoAction(undoTask, "Undo Batch Insert"));
        return task;
    }

    private TaskWithArgsReturn<List<Transaction>, List<Transaction>> generateBatchInsertTransaction(List<Transaction> transactions) {
        TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = new TaskWithArgsReturn<>((transactionList) -> {
            try {
                List<Transaction> list = new ArrayList<>();
                db.setDatabaseAutoCommit(false);

                for (Transaction currentTransaction : transactionList) {
                    try {
                        db.insertTransaction(currentTransaction);
                    } catch (StorageException e) {
                        System.out.println("ERROR - DbContoller.batchInsertTransaction: Adding transaction failed \n" + e.getMessage());
                        list.add(currentTransaction);
                    }
                }
                return list;
            } finally {
                db.setDatabaseAutoCommit(true);
            }
        }, transactions);

        for (CallableMethodVoidNoArgs method : transactionSuccessEvent) {
            task.RegisterSuccessEvent((t) -> method.call());
        }

        for (CallableMethodVoidNoArgs method : payeeSuccessEvent) {
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

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to check transactions as a batch with Ignore checks
     *
     * @param transactions List of transactions to add to the database
     * @return A task for the asynchronous call
     */
    public TaskWithArgsReturn<List<Transaction>, List<Transaction>> batchTransactionIgnoreCheck(List<Transaction> transactions) {
        return new TaskWithArgsReturn<>((transactionList) -> {
            List<Transaction> list = new ArrayList<>();
            for (Transaction currentTransaction : transactionList) {

                try {
                    if (db.isTransactionIgnored(currentTransaction))
                        list.add(currentTransaction);
                } catch (StorageException ignored) {
                }
            }
            return list;
        }, transactions);
    }

    protected IDatabase getDb() {
        return db;
    }


    /**
     * Shows the message from the action on top of the stack.
     * @return the String or Null or the stack is empty
     */
    public String undoPeekMessage() {
        if(undoStack.isEmpty())
            return null;
        return undoStack.peek().getMessage();
    }

    /**
     * Undoes the top Action on the stack.
     */
    public void undo() {
        undoStack.pop().undo();
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
        return new TaskWithReturn<>(db::getAllIgnoredExpressions);
    }


}
