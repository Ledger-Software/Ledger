package ledger.io.input;

import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.SQLite.SQLiteDatabase;
import ledger.exception.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jesse Shellabarger on 10/16/2016.
 */
public class DuplicateDetectorTest {

    private static IDatabase database;
    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Type sampleType;
    private static Account sampleAccount;
    private static Payee samplePayee;
    private static Tag sampleTag;
    private static Tag sampleTag2;
    private static Note sampleNote;

    @Before
    public void setupTestData() throws Exception {
        database = new SQLiteDatabase("src/test/resources/test.db");

        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleTag2 = new Tag("Electronics", "Money spent on electronics");
        sampleNote = new Note("This is a note");

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);
        sampleTagList.add(sampleTag2);

        sampleTransaction1 = new Transaction(new Date(), sampleType, 4201, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction2 = new Transaction(new Date(), sampleType, 103, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction3 = new Transaction(new Date(), sampleType, 3304, sampleAccount, samplePayee, false, sampleTagList, sampleNote);

        database.insertType(sampleType);
    }

    @Test
    public void detectionWithNoDuplicatesTest() throws StorageException {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(sampleTransaction1);
        transactions.add(sampleTransaction2);
        transactions.add(sampleTransaction3);

        DuplicateDetector dupeDetector = new DuplicateDetector(transactions);
        DetectionResult result = null;

        try {
            result = dupeDetector.detectDuplicates(database);
        } catch (StorageException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(0, result.getPossibleDuplicates().size());
        assertEquals(3, result.getVerifiedTransactions().size());
    }

    @Test
    public void detectionWithNoDuplicatesNotEmptyTest() throws StorageException {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(sampleTransaction1);
        transactions.add(sampleTransaction2);

        database.insertTransaction(sampleTransaction3);

        DuplicateDetector dupeDetector = new DuplicateDetector(transactions);
        DetectionResult result = null;

        try {
            result = dupeDetector.detectDuplicates(database);
        } catch (StorageException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(0, result.getPossibleDuplicates().size());
        assertEquals(2, result.getVerifiedTransactions().size());
    }

    @Test
    public void detectionWithDuplicatesTest() throws StorageException {
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(sampleTransaction1);
        transactions.add(sampleTransaction2);
        transactions.add(sampleTransaction3);

        database.insertTransaction(sampleTransaction2);

        DuplicateDetector dupeDetector = new DuplicateDetector(transactions);
        DetectionResult result = null;

        try {
            result = dupeDetector.detectDuplicates(database);
        } catch (StorageException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(1, result.getPossibleDuplicates().size());
        assertEquals(2, result.getVerifiedTransactions().size());
    }

    @After
    public void afterTests() throws Exception {
        database.shutdown();

        Path dbPath = Paths.get("src/test/resources/test.db");
        Files.delete(dbPath);
    }
}