package ledger.io.importer;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.database.storage.SQLiteDatabase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Tayler How on 10/11/2016.
 */
public class ImporterTest {

    private static IDatabase database;
    private static Importer importer;
    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Transaction failingTransaction1;
    private static Transaction failingTransaction2;
    private static Type sampleType;
    private static Type sampleType2;
    private static Account sampleAccount;
    private static Payee samplePayee;
    private static Tag sampleTag;
    private static Note sampleNote;
    private static List<Transaction> allTransactions;
    private static List<Transaction> succeedingTransactions;
    private static List<Transaction> failingTransactions;

    @BeforeClass
    public static void setupDatabaseAndImporter() throws Exception {
        database = new SQLiteDatabase("src/test/resources/test.db");
        importer = Importer.getInstance();
    }

    @Before
    public void setupTestData() throws Exception{
        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleType2 = new Type("Debit", "Purchased with a debit card");
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleNote = new Note("This is a note");

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        sampleTransaction1 = new Transaction(new Date(), sampleType, 4201, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction2 = new Transaction(new Date(), sampleType, 103, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction3 = new Transaction(new Date(), sampleType, 3304, sampleAccount, samplePayee, false, sampleTagList, sampleNote);

        failingTransaction1 = new Transaction(new Date(), sampleType2, 27891, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        failingTransaction2 = new Transaction(new Date(), sampleType2, 1993, sampleAccount, samplePayee, true, sampleTagList, sampleNote);

        allTransactions = new ArrayList<>();
        allTransactions.add(sampleTransaction1);
        allTransactions.add(sampleTransaction2);
        allTransactions.add(sampleTransaction3);
        allTransactions.add(failingTransaction1);
        allTransactions.add(failingTransaction2);

        succeedingTransactions = new ArrayList<>();
        succeedingTransactions.add(sampleTransaction1);
        succeedingTransactions.add(sampleTransaction2);
        succeedingTransactions.add(sampleTransaction3);

        failingTransactions = new ArrayList<>();
        failingTransactions.add(failingTransaction1);
        failingTransactions.add(failingTransaction2);

        database.insertType(sampleType);
    }

    @Test
    public void importTransactionSuccess() throws Exception {
        int transactionCountBefore = database.getAllTransactions().size();

        boolean importSuccess = importer.importTransactions(database, succeedingTransactions);

        assertTrue(importSuccess);

        List<Transaction> transactionsAfterImport = database.getAllTransactions();

        assertEquals(transactionCountBefore + succeedingTransactions.size(), transactionsAfterImport.size());

        //pull out ids
        List<Integer> ids = transactionsAfterImport.stream().map(Transaction::getId).collect(Collectors.toList());

        for (Transaction succeedingTransaction : succeedingTransactions) {
            assertTrue(ids.contains(succeedingTransaction.getId()));
        }
    }

    @Test
    public void importTransactionFailure() throws Exception {
        int transactionCountBefore = database.getAllTransactions().size();

        boolean importSuccess = importer.importTransactions(database, allTransactions);

        assertFalse(importSuccess);

        // Check that valid transactions were successfully imported
        List<Transaction> transactionsAfterImport = database.getAllTransactions();

        assertEquals(transactionCountBefore + succeedingTransactions.size(), transactionsAfterImport.size());

        //pull out ids
        List<Integer> ids = transactionsAfterImport.stream().map(Transaction::getId).collect(Collectors.toList());

        for (Transaction succeedingTransaction : succeedingTransactions) {
            assertTrue(ids.contains(succeedingTransaction.getId()));
        }

        // Check that all invalid transactions are put into failedImports list
        assertEquals(failingTransactions, importer.getFailedImports());
    }

    @AfterClass
    public static void afterTests() throws Exception {
        database.shutdown();

        Path dbPath = Paths.get("src/test/resources/test.db");
        Files.delete(dbPath);
    }
}
