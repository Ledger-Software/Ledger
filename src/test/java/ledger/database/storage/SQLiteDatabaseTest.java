package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.*;

public class SQLiteDatabaseTest {

    private IDatabase database;
    private Transaction sampleTransaction1;
    private Transaction sampleTransaction2;
    private Transaction sampleTransaction3;
    private Type sampleType;
    private Account sampleAccount;
    private Payee samplePayee;
    private Tag sampleTag;
    private Note sampleNote;

    @BeforeClass
    public void setupSampleObjects() {
        this.sampleType = new Type("Credit", "Purchased with a credit card");
        this.sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        this.samplePayee = new Payee("Meijer", "Grocery store");
        this.sampleTag = new Tag("Groceries", "Money spent on groceries");
        this.sampleNote = new Note("This is a note");

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(this.sampleTag);

        this.sampleTransaction1 = new Transaction(new Date(), this.sampleType, 4201, this.sampleAccount, this.samplePayee, true, sampleTagList, this.sampleNote);
        this.sampleTransaction2 = new Transaction(new Date(), this.sampleType, 103, this.sampleAccount, this.samplePayee, true, sampleTagList, this.sampleNote);
        this.sampleTransaction3 = new Transaction(new Date(), this.sampleType, 3304, this.sampleAccount, this.samplePayee, true, sampleTagList, this.sampleNote);
    }

    @Before
    public void setupDatabase() throws Exception {
        database = new SQLiteDatabase(null, "jdbc:sqlite:src/test/resources/test.db");
    }

    @Test
    public void insertTransaction() throws Exception {

    }

    @Test
    public void deleteTransaction() throws Exception {
        database.insertTransaction(this.sampleTransaction1);
        database.insertTransaction(this.sampleTransaction2);
        database.insertTransaction(this.sampleTransaction3);

        List<Transaction> transactionsBeforeDelete = database.getAllTransactions();
        int countBeforeDelete = transactionsBeforeDelete.size();

        Transaction transactionToDelete = transactionsBeforeDelete.get(0);
        database.deleteTransaction(transactionToDelete);

        List<Transaction> transactionsAfterDelete = database.getAllTransactions();
        int countAfterDelete = transactionsAfterDelete.size();

        assertEquals(countBeforeDelete - 1, countAfterDelete);

        ArrayList<Integer> IDsAfterDelete = new ArrayList<>();
        for (Transaction currentTransaction : transactionsAfterDelete) {
            IDsAfterDelete.add(currentTransaction.getId());
        }

        assertFalse(IDsAfterDelete.contains(transactionToDelete.getId()));
    }

    @Test
    public void editTransaction() throws Exception {

    }

    @Test
    public void getAllTransactions() throws Exception {

    }

    @Test
    public void insertAccount() throws Exception {

    }

    @Test
    public void deleteAccount() throws Exception {

    }

    @Test
    public void editAccount() throws Exception {

    }

    @Test
    public void insertPayee() throws Exception {

    }

    @Test
    public void deletePayee() throws Exception {

    }

    @Test
    public void editPayee() throws Exception {

    }

    @Test
    public void insertNote() throws Exception {

    }

    @Test
    public void deleteNote() throws Exception {

    }

    @Test
    public void editNote() throws Exception {

    }

    @Test
    public void insertType() throws Exception {

    }

    @Test
    public void deleteType() throws Exception {

    }

    @Test
    public void editType() throws Exception {

    }

    @Test
    public void insertTag() throws Exception {

    }

    @Test
    public void deleteTag() throws Exception {

    }

    @Test
    public void editTag() throws Exception {

    }

    @After
    public void afterTests() throws Exception {
        database.shutdown();
    }
}
