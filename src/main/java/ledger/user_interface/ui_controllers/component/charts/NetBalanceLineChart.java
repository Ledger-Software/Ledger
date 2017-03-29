package ledger.user_interface.ui_controllers.component.charts;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import ledger.controller.DbController;
import ledger.controller.register.Task;
import ledger.database.entity.Account;
import ledger.database.entity.AccountBalance;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.component.FilteringAccountDropdown;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by CJ on 3/26/2017.
 */
public class NetBalanceLineChart extends LineChart<Long, Double> implements IChart {

    private FilteringAccountDropdown accountDropdown;

    private static Axis defaultXAxis() {
        Axis xAxis = new DateAxis();
        xAxis.setSide(Side.BOTTOM);

        return xAxis;
    }

    private static Axis defaultYAxis() {
        Axis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);

        return yAxis;
    }

    public NetBalanceLineChart(List<Transaction> transactionList, FilteringAccountDropdown accountObjectProperty) {
        this(defaultXAxis(),defaultYAxis());
        this.accountDropdown = accountObjectProperty;
        this.updateData(transactionList);
    }

    public NetBalanceLineChart(@NamedArg("xAxis") Axis<Long> stringAxis, @NamedArg("yAxis") Axis<Double> doubleAxis) {
        super(stringAxis, doubleAxis);
    }

    @Override
    public void updateData(List<Transaction> transactionList) {
        Map<Long, Double> dataMap = new HashMap<>();

        transactionList.sort(Comparator.comparing(Transaction::getDate));

        double runningTotal = 0;

        Account account = accountDropdown.getSelectedAccount();
        if(account == null) {
            Task<List<Account>> accountsTask = DbController.INSTANCE.getAllAccounts();
            accountsTask.startTask();
            List<Account> accounts = accountsTask.waitForResult();

            for(Account a: accounts) {
                Task<AccountBalance> balanceTask = DbController.INSTANCE.getBalanceForAccount(a);
                balanceTask.startTask();
                AccountBalance balance = balanceTask.waitForResult();

                runningTotal += balance.getAmount() / 100.0;
            }

            Task<List<Transaction>> transactionTask = DbController.INSTANCE.getAllTransactions();
            transactionTask.startTask();
            List<Transaction> allTransactions = transactionTask.waitForResult();

            if(transactionList.size() > 0) {
                Transaction firstTransaction = transactionList.get(0);

                runningTotal += sumOldTransactions(firstTransaction, allTransactions, transactionList);
            }
        } else {
            Task<AccountBalance> task = DbController.INSTANCE.getBalanceForAccount(account);
            task.startTask();
            AccountBalance balance = task.waitForResult();
            runningTotal += balance.getAmount() / 100.0;

            Task<List<Transaction>> transactionTask = DbController.INSTANCE.getAllTransactionsForAccount(account);
            transactionTask.startTask();
            List<Transaction> allTransactions = transactionTask.waitForResult();

            if(transactionList.size() > 0) {
                Transaction firstTransaction = transactionList.get(0);

                runningTotal += sumOldTransactions(firstTransaction, allTransactions, transactionList);
            }
        }

        for(Transaction transaction: transactionList) {
            runningTotal += transaction.getAmount() / 100.0;

            Date date = transaction.getDate();

            long mills = date.toInstant().toEpochMilli();

            if(dataMap.containsKey(mills)) {
                dataMap.put(mills, dataMap.get(mills) + runningTotal);
            } else {
                dataMap.put(mills, runningTotal);
            }
        }

        XYChart.Series series = new XYChart.Series();
        for (long time : dataMap.keySet()) {
            series.getData().add(new XYChart.Data(time, dataMap.get(time)));
        }
        series.setName("Net Balance");
        this.getData().setAll(series);
    }

    private double sumOldTransactions(Transaction firstTransaction, List<Transaction> allTransactionList, List<Transaction> excludedTransactions) {
        double toReturn = 0;
        for(Transaction transaction: allTransactionList) {
            if(transaction.getDate().after(firstTransaction.getDate())) continue;
            if(excludedTransactions.contains(transaction)) continue;

            toReturn +=  transaction.getAmount() / 100.0;
        }

        return toReturn;
    }
}
