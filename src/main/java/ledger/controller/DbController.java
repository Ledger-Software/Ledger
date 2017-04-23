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
    private List<CallableMethod> transactionSuccessEvent;
    private List<CallableMethod> accountSuccessEvent;
    private List<CallableMethod> payeeSuccessEvent;
    private List<CallableMethod> ignoredExpSuccessEvent;

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

    public void registerTransactionSuccessEvent(CallableMethod method) {
        transactionSuccessEvent.add(method);
    }

    public void registerAccountSuccessEvent(CallableMethod method) {
        accountSuccessEvent.add(method);
    }

    public void registerPayeeSuccessEvent(CallableMethod method) {
        payeeSuccessEvent.add(method);
    }

    public void registerIgnoredExpressionSuccessEvent(CallableMethod method) {
        ignoredExpSuccessEvent.add(method);
    }

    private void registerSuccess(Task task, List<CallableMethod> methods) {
        methods.forEach(task::RegisterSuccessEvent);
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to insert a Transaction
     *
     * @param transaction The transaction to insert
     * @return a ITask for the Async Call
     */
    public TaskNoReturn insertTransaction(final Transaction transaction) {
        TaskNoReturn task = generateInsertTransaction(transaction);

        undoStack.push(new UndoAction(generateDeleteTransaction(transaction), "Undo Insert Transaction"));
        return task;
    }

    private TaskNoReturn generateInsertTransaction(final Transaction transaction) {
        TaskNoReturn task = new TaskNoReturn(() -> db.insertTransaction(transaction));

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
    public TaskNoReturn deleteTransaction(final Transaction transaction) {
        TaskNoReturn task = generateDeleteTransaction(transaction);

        undoStack.push(new UndoAction(generateInsertTransaction(transaction), "Undo Delete Transaction"));
        return task;

    }

    private TaskNoReturn generateDeleteTransaction(final Transaction transaction) {
        TaskNoReturn task = new TaskNoReturn(() -> db.deleteTransaction(transaction));
        registerSuccess(task, transactionSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Transaction
     *
     * @param transaction The transaction to edit
     * @return a ITask for the Async Call
     */
    public TaskNoReturn editTransaction(final Transaction transaction) {
        TaskNoReturn task = generateEditTransaction(transaction);

        Transaction oldTrans = null;
        try {
            oldTrans = db.getTransactionById(transaction);
        } catch (StorageException e) {
            System.err.println("Error on getTransactionById");
        }

        undoStack.push(new UndoAction(generateEditTransaction(oldTrans), "Undo Edit Transaction"));

        return task;
    }

    private TaskNoReturn generateEditTransaction(final Transaction transaction) {
        TaskNoReturn task = new TaskNoReturn(() -> db.editTransaction(transaction));
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
    public TaskNoReturn insertAccount(final Account account) {
        TaskNoReturn task = generateInsertAccount(account);

        undoStack.push(new UndoAction(generateDeleteAccount(account), "Undo Insert Account"));

        return task;
    }

    private TaskNoReturn generateInsertAccount(final Account account) {
        TaskNoReturn task = new TaskNoReturn(() -> db.insertAccount(account));
        registerSuccess(task, accountSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete an Account
     *
     * @param account The account to delete
     * @return a ITask for the Async Call
     */
    public TaskNoReturn deleteAccount(final Account account) {
        TaskNoReturn task = generateDeleteAccount(account);

        AccountBalance ab = null;
        try {
            ab = db.getBalanceForAccount(account);
        } catch (StorageException ignored) {
        }

        List<Transaction> trans;
        try {
            trans = db.getAllTransactionsForAccount(account);
        } catch (StorageException e) {
            trans = new ArrayList<>();
        }

        if (trans.size() != 0) {
            final AccountBalance finalAb = ab;
            final List<Transaction> finalTrans = trans;
            TaskNoReturn undoTask = new TaskNoReturn(() -> {
                db.insertAccount(account);
                for (Transaction t : finalTrans) {
                    t.setAccount(account);
                    db.insertTransaction(t);
                }
                finalAb.setAccount(account);
                db.addBalanceForAccount(finalAb);
            });
            registerSuccess(undoTask, transactionSuccessEvent);
            registerSuccess(undoTask, accountSuccessEvent);
            undoStack.push(new UndoAction(undoTask, "Undo Delete Account and Transactions"));
        } else {
            final AccountBalance finalAb1 = ab;
            TaskNoReturn undoTask = new TaskNoReturn(() -> {
                db.insertAccount(account);
                finalAb1.setAccount(account);
                db.addBalanceForAccount(finalAb1);
            });
            registerSuccess(undoTask, transactionSuccessEvent);
            registerSuccess(undoTask, accountSuccessEvent);
            undoStack.push(new UndoAction(undoTask, "Undo Delete Account"));
        }

        return task;
    }

    private TaskNoReturn generateDeleteAccount(final Account account) {
        TaskNoReturn task = new TaskNoReturn(() -> db.deleteAccount(account));
        registerSuccess(task, accountSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit an Account
     *
     * @param account The account to edit
     * @return a ITask for the Async Call
     */
    public TaskNoReturn editAccount(final Account account) {
        TaskNoReturn task = generateEditAccount(account);

        Account oldAccount = null;
        try {
            oldAccount = db.getAccountById(account);
        } catch (StorageException ignored) {
        }

        undoStack.push(new UndoAction(generateEditAccount(oldAccount), "Undo Edit Transaction"));

        return task;
    }

    private TaskNoReturn generateEditAccount(final Account account) {
        TaskNoReturn task = new TaskNoReturn(() -> db.editAccount(account));
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
    public TaskNoReturn insertPayee(final Payee payee) {
        TaskNoReturn task = generateInsertPayee(payee);
        undoStack.push(new UndoAction(generateDeletePayee(payee), "Undo Insert Payee"));
        return task;

    }

    private TaskNoReturn generateInsertPayee(final Payee payee) {
        TaskNoReturn task = new TaskNoReturn(() -> db.insertPayee(payee));
        registerSuccess(task, payeeSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to delete a Payee
     *
     * @param payee The payee to delete
     * @return a ITask for the Async Call
     */
    public TaskNoReturn deletePayee(final Payee payee) {
        TaskNoReturn task = generateDeletePayee(payee);
        undoStack.push(new UndoAction(generateInsertPayee(payee), "Undo Delete Payee"));
        return task;
    }

    private TaskNoReturn generateDeletePayee(final Payee payee) {
        TaskNoReturn task = new TaskNoReturn(() -> db.deletePayee(payee));
        registerSuccess(task, payeeSuccessEvent);
        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Payee
     *
     * @param payee The payee to edit
     * @return a ITask for the Async Call
     */
    public TaskNoReturn editPayee(final Payee payee) {
        TaskNoReturn task = generateEditPayee(payee);

        Payee oldPayee = null;
        try {
            oldPayee = db.getPayeeById(payee);
        } catch (StorageException ignored) {
        }

        undoStack.push(new UndoAction(generateEditPayee(oldPayee), "Undo Edit Payee"));

        return task;
    }

    private TaskNoReturn generateEditPayee(final Payee payee) {
        TaskNoReturn task = new TaskNoReturn(() -> db.editPayee(payee));
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
     * Creates a ITask that can be used to make an asynchronous call to the database to edit a Tag
     *
     * @param tag The tag to edit
     * @return a ITask for the Async Call
     */
    public TaskNoReturn editTag(final Tag tag) {
        TaskNoReturn task = generateEditTag(tag);

        Tag oldTag = null;
//        try {
        oldTag = db.getTagForNameAndDescription(tag.getName(), tag.getDescription());
//        } catch (StorageException ignored) {
//        }

        undoStack.push(new UndoAction(generateEditTag(oldTag), "Undo Edit Payee"));

        return task;
    }

    private TaskNoReturn generateEditTag(final Tag tag) {
        TaskNoReturn task = new TaskNoReturn(() -> db.editTag(tag));
        registerSuccess(task, transactionSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all stored Payees
     *
     * @return A task for the Async Call that returns a list of all the Payees
     */
    public TaskWithReturn<List<Tag>> getAllTags() {
        return new TaskWithReturn<>(db::getAllTags);

    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get all Tags associated with a
     * Payee
     *
     * @param payee The Payee to get all associated tags for
     * @return A task for the Async call that returns a list of all the Tags associated with the Payee
     */
    public TaskWithReturn<List<Tag>> getTagsForPayee(final Payee payee) {
        return new TaskWithReturn<>(() -> db.getAllTagsForPayee(payee));
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
    public TaskWithReturn<List<Transaction>> batchInsertTransaction(List<Transaction> transactions) {
        TaskWithReturn<List<Transaction>> task = generateBatchInsertTransaction(transactions);

        List<Transaction> copyList = Lists.newArrayList(transactions);

        TaskNoReturn undoTask = new TaskNoReturn(() -> {
            try {
                db.setDatabaseAutoCommit(false);

                for (Transaction currentTransaction : copyList) {
                    try {
                        db.deleteTransaction(currentTransaction);
                    } catch (StorageException ignored) {
                    }
                }
            } finally {
                db.setDatabaseAutoCommit(true);
            }
        });
        registerSuccess(undoTask, transactionSuccessEvent);

        undoStack.push(new UndoAction(undoTask, "Undo Batch Insert"));
        return task;
    }

    private TaskWithReturn<List<Transaction>> generateBatchInsertTransaction(List<Transaction> transactions) {
        TaskWithReturn<List<Transaction>> task = new TaskWithReturn<>(() -> {
            try {
                List<Transaction> list = new ArrayList<>();
                db.setDatabaseAutoCommit(false);

                for (Transaction currentTransaction : transactions) {
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
        });

        registerSuccess(task, transactionSuccessEvent);
        registerSuccess(task, payeeSuccessEvent);

        return task;
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to get the balance for a given account
     *
     * @param account The account to get the AccountBalance for
     * @return A task for the asynchronous call
     */
    public TaskWithReturn<AccountBalance> getBalanceForAccount(Account account) {
        return new TaskWithReturn<>(() -> db.getBalanceForAccount(account));
    }

    /**
     * Creates a ITask that can be used to make an asynchronous call to the database to add an account balance for a given
     * account
     *
     * @param balance AccountBalance to add to the database
     * @return A task for the asynchronous call
     */
    public TaskNoReturn addBalanceForAccount(AccountBalance balance) {
        return new TaskNoReturn(() -> db.addBalanceForAccount(balance));
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to check transactions as a batch with Ignore checks
     *
     * @param transactions List of transactions to add to the database
     * @return A task for the asynchronous call
     */
    public TaskWithReturn<List<Transaction>> batchTransactionIgnoreCheck(List<Transaction> transactions) {
        return new TaskWithReturn<>(() -> {
            List<Transaction> list = new ArrayList<>();
            for (Transaction currentTransaction : transactions) {

                try {
                    if (db.isTransactionIgnored(currentTransaction))
                        list.add(currentTransaction);
                } catch (StorageException ignored) {
                }
            }
            return list;
        });
    }

    protected IDatabase getDb() {
        return db;
    }


    /**
     * Shows the message from the action on top of the stack.
     *
     * @return the String or Null or the stack is empty
     */
    public String undoPeekMessage() {
        if (undoStack.isEmpty())
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
    public TaskNoReturn insertIgnoredExpression(IgnoredExpression igEx) {
        TaskNoReturn task = new TaskNoReturn(() -> db.insertIgnoredExpression(igEx));
        registerSuccess(task, ignoredExpSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to edit an Ignored Expression
     *
     * @param igEx the IgnoredExpression to edit
     * @return a Task for the Async Call
     */
    public TaskNoReturn editIgnoredExpression(IgnoredExpression igEx) {
        TaskNoReturn task = new TaskNoReturn(() -> db.editIgnoredExpression(igEx));
        registerSuccess(task, ignoredExpSuccessEvent);
        return task;
    }

    /**
     * Creates a Task that can be used to make an asynchronous call to the database to delete an Ignored Expression
     *
     * @param igEx the IgnoredExpression to delete
     * @return a Task for the Async Call
     */
    public TaskNoReturn deleteIgnoredExpression(IgnoredExpression igEx) {
        TaskNoReturn task = new TaskNoReturn(() -> db.deleteIgnoredExpression(igEx));
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
