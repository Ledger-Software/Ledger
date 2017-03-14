package ledger.user_interface.utils;

import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.AccountBalance;
import ledger.database.entity.Transaction;
import ledger.exception.StorageException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tayler How on 3/12/2017.
 */
public class TransactionRunningAccountBalanceCalculator {

    public static List<Transaction> getAllTransactionsWithRunningBalances() throws StorageException {
        TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactions();
        task.RegisterFailureEvent((e) -> {
            throw new StorageException("Unable to fetch transactions from the database.");
        });
        task.startTask();
        List<Transaction> transactions = task.waitForResult();

        sortTransactionsAndCalculateBalances(transactions);
        return transactions;
    }

    public static List<Transaction> getTransactionsWithRunningBalancesForAccount(Account account) throws StorageException {
        TaskWithReturn<List<Transaction>> task = DbController.INSTANCE.getAllTransactionsForAccount(account);
        task.RegisterFailureEvent((e) -> {
            throw new StorageException("Unable to fetch transactions from the database.");
        });
        task.startTask();
        List<Transaction> transactions = task.waitForResult();

        sortTransactionsAndCalculateBalances(transactions);
        return transactions;
    }

    private static void sortTransactionsAndCalculateBalances(List<Transaction> transactions) throws StorageException {
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                int value1 = t1.getAccount().compareTo(t2.getAccount());
                if (value1 == 0) {
                    int value2 = t1.getDate().compareTo(t2.getDate());
                    if (value2 == 0) {
                        return ((Integer) t1.getId()).compareTo(t2.getId());
                    } else {
                        return value2;
                    }
                } else {
                    return value1;
                }
            }
        });

        Transaction currentTransaction;
        Transaction previousTransaction;
        for (int i = 0; i < transactions.size(); i++) {
            currentTransaction = transactions.get(i);
            if (i == 0) {
                setRunningBalanceForFirstTransactionInAccount(currentTransaction);
            } else {
                previousTransaction = transactions.get(i - 1);
                if (previousTransaction.getAccount().equals(currentTransaction.getAccount())) {
                    currentTransaction.setRunningBalance(previousTransaction.getRunningBalance() + currentTransaction.getAmount());
                } else {
                    setRunningBalanceForFirstTransactionInAccount(currentTransaction);
                }
            }
        }
    }

    private static void setRunningBalanceForFirstTransactionInAccount(Transaction transaction) throws StorageException {
        TaskWithReturn<AccountBalance> balanceTask = DbController.INSTANCE.getBalanceForAccount(transaction.getAccount());
        balanceTask.RegisterFailureEvent((e) -> {
            throw new StorageException("Unable to fetch current account balance from the database.");
        });
        balanceTask.startTask();
        AccountBalance balance = balanceTask.waitForResult();
        transaction.setRunningBalance(balance.getAmount() + transaction.getAmount());
    }
}
