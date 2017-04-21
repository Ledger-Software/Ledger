package ledger.database.controller;

import ledger.controller.DbController;
import ledger.controller.register.Task;
import ledger.controller.register.TaskNoReturn;
import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.SQLite.SQLiteDatabase;
import ledger.io.importer.Importer;
import ledger.io.input.TypeConversion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gert on 3/27/17.
 */
public class ControllerTransferTest {

    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Transaction sampleTransaction4;
    private static Type sampleType;
    private static Type sampleType2;
    private static Type transferType;
    private static Account sampleAccount;
    private static Account sampleAccount2;
    private static Payee samplePayee;
    private static Payee samplePayee2;
    private static Payee samplePayee3;
    private static Tag sampleTag;
    private static Tag sampleTag2;
    private static Note sampleNote;
    private static Note sampleNote2;
    private static Note sampleNote3;

    @BeforeClass
    public static void setupDatabaseAndController() throws Exception {
        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleType2 = new Type("Debit", "Purchased with a debit card");
        transferType = TypeConversion.ACC_TRANSFER;
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        sampleAccount2 = new Account("US Bank", "Debit account with US Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        samplePayee2 = new Payee("Kroger", "Grocery store");
        samplePayee3 = new Payee("Wal-Mart", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleTag2 = new Tag("Electronics", "Money spent on electronics");
        sampleNote = new Note("This is a note");
        sampleNote2 = new Note("This is also a note");
        sampleNote3 = new Note("This is also a note, ditto");
        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        sampleTransaction1 = new Transaction(new Date(), transferType, 4201, sampleAccount,  new Payee("Transfer Payee",""), false, sampleTagList, sampleNote, -1, -1, sampleAccount);
        sampleTransaction2 = new Transaction(new Date(), transferType, 103, sampleAccount,  new Payee("Transfer Payee",""), false, sampleTagList, sampleNote2, -1, -1, sampleAccount);
        sampleTransaction3 = new Transaction(new Date(), transferType, 3304, sampleAccount,  new Payee("Transfer Payee",""), false, sampleTagList, sampleNote3, -1, -1, sampleAccount);
        sampleTransaction4 = new Transaction(new Date(), transferType, 3321, sampleAccount, new Payee("Transfer Payee",""), false, sampleTagList, sampleNote3, -1, -1, sampleAccount);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2011, Calendar.OCTOBER, 1);
        long secondsSinceEpoch = calendar.getTimeInMillis() / 1000L;

        DbController.INSTANCE.initialize(new File("src/test/resources/controllerTest.db").getAbsolutePath(),"password");

        TaskNoReturn insertAccount = DbController.INSTANCE.insertAccount(sampleAccount);
        insertAccount.startTask();
        insertAccount.waitForComplete();
        insertAccount = DbController.INSTANCE.insertAccount(sampleAccount2);
        insertAccount.startTask();
        insertAccount.waitForComplete();
    }
    @Test
    public void testInsertTransfer(){
        final int currentsize;
        Task<List<Transaction>> getAllTransactions = DbController.INSTANCE.getAllTransactions();
        getAllTransactions.startTask();
        List<Transaction> transactions = getAllTransactions.waitForResult();
        currentsize = transactions.size();
        TaskNoReturn insertTransfer = DbController.INSTANCE.insertTransaction(sampleTransaction4);
        insertTransfer.startTask();
        insertTransfer.waitForComplete();
        getAllTransactions = DbController.INSTANCE.getAllTransactions();

        getAllTransactions.RegisterSuccessEvent((transactionlist) -> {
            assertEquals(transactionlist.size(),currentsize+2);
        });
        getAllTransactions.startTask();
        getAllTransactions.waitForComplete();

    }
    @Test
    public void testDeleteTransfer(){
        final int currentsize;
        Task<List<Transaction>> getAllTransactions = DbController.INSTANCE.getAllTransactions();
        getAllTransactions.startTask();
        List<Transaction> transactions = getAllTransactions.waitForResult();
        currentsize = transactions.size();
        TaskNoReturn insertTransfer = DbController.INSTANCE.insertTransaction(sampleTransaction1);
        insertTransfer.startTask();
        insertTransfer.waitForComplete();
        getAllTransactions = DbController.INSTANCE.getAllTransactions();
        getAllTransactions.RegisterSuccessEvent((transaction) -> {
            assertEquals(transaction.size(),currentsize+2);
        });
        getAllTransactions.startTask();
         transactions = getAllTransactions.waitForResult();
        TaskNoReturn deleteTransfer = DbController.INSTANCE.deleteTransaction(transactions.get(0));
        deleteTransfer.startTask();
        deleteTransfer.waitForComplete();
        getAllTransactions = DbController.INSTANCE.getAllTransactions();
        getAllTransactions.RegisterSuccessEvent((transactionlist) -> {
            assertEquals(transactionlist.size(),currentsize);
        });
        getAllTransactions.startTask();
        getAllTransactions.waitForComplete();
    }
    @Test
    public void testEditTransfer(){
        int currentsize;
        Task<List<Transaction>> getAllTransactions = DbController.INSTANCE.getAllTransactions();
        getAllTransactions.startTask();
        List<Transaction> transactions = getAllTransactions.waitForResult();
        currentsize = transactions.size();
        TaskNoReturn insertTransfer = DbController.INSTANCE.insertTransaction(sampleTransaction2);
        insertTransfer.startTask();
        insertTransfer.waitForComplete();
        getAllTransactions = DbController.INSTANCE.getAllTransactions();

        getAllTransactions.RegisterSuccessEvent((transactionlist) -> {
            assertEquals(transactionlist.size(),currentsize+2);
        });
        getAllTransactions.startTask();
        transactions = getAllTransactions.waitForResult();
        Transaction transactionToEdit = transactions.get(currentsize);
        transactionToEdit.setAmount(90032);
        TaskNoReturn editTransfer = DbController.INSTANCE.editTransaction(transactionToEdit);
        editTransfer.startTask();
        editTransfer.waitForComplete();
        getAllTransactions = DbController.INSTANCE.getAllTransactions();

        getAllTransactions.RegisterSuccessEvent((transactionlist) -> {
            assertEquals(transactionlist.size(),
                    currentsize+2);
        });
        getAllTransactions.startTask();
        transactions = getAllTransactions.waitForResult();
        assertEquals(transactions.get(currentsize+1).getAmount(),-90032);
    }
    @AfterClass
    public static void afterTests() throws Exception {
        DbController.INSTANCE.shutdown();
        Path dbPath = new File("src/test/resources/controllerTest.db.mv.db").toPath();
        Files.delete(dbPath);
    }
}
