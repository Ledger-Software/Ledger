package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import org.junit.*;
import ledger.database.enity.Transaction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

import java.util.*;
import java.util.stream.Collectors;

public class SQLiteDatabaseTest {

    private static IDatabase database;
    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Type sampleType;
    private static Account sampleAccount;
    private static Payee samplePayee;
    private static Tag sampleTag;
    private static Note sampleNote;

    @BeforeClass
    public static void setupSampleObjects() {
        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleNote = new Note("This is a note");

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        sampleTransaction1 = new Transaction(new Date(), sampleType, 4201, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction2 = new Transaction(new Date(), sampleType, 103, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction3 = new Transaction(new Date(), sampleType, 3304, sampleAccount, samplePayee, false, sampleTagList, sampleNote);
    }

    @Before
    public void setupDatabase() throws Exception {
        database = new SQLiteDatabase(null, "src/test/resources/test.db");
    }

    @Test
    public void insertTransaction() throws Exception {
        database.insertTransaction(sampleTransaction1);

        List<Transaction> trans = database.getAllTransactions();

        assertEquals(1, trans.size());
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
        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(this.sampleTag);

        Transaction originalTransaction = new Transaction(new Date(), this.sampleType, 1202, this.sampleAccount, this.samplePayee, false, sampleTagList, this.sampleNote);

        database.insertTransaction(originalTransaction);

        List<Transaction> transactions = database.getAllTransactions();
        Transaction transactionToEdit = transactions.get(0);

        int amountToSet = 99999;
        transactionToEdit.setAmount(amountToSet);
        database.editTransaction(transactionToEdit);

        transactions = database.getAllTransactions();
        Transaction editedTransaction = transactions.get(0);

        assertEquals(amountToSet, editedTransaction.getAmount());
    }

    @Test
    public void getAllTransactions() throws Exception {
        database.insertTransaction(sampleTransaction1);
        database.insertTransaction(sampleTransaction2);
        database.insertTransaction(sampleTransaction3);

        List<Transaction> trans = database.getAllTransactions();

        //pull out ids
        assertEquals(3, trans.size());

        List<Integer> ids = trans.stream().map(elm -> elm.getAmount()).collect(Collectors.toList());

        //using amounts to test, since we don't know the transaction ID's and the amounts happen to be unique
        assertTrue(ids.contains(sampleTransaction1.getAmount()));
        assertTrue(ids.contains(sampleTransaction2.getAmount()));
        assertTrue(ids.contains(sampleTransaction3.getAmount()));
    }

    @Test
    public void insertAccount() throws Exception {
        List<Account> accounts = database.getAllAccounts();
        assertEquals(0, accounts.size());

        database.insertAccount(sampleAccount);

        accounts = database.getAllAccounts();
        assertEquals(1, accounts.size());

        assertEquals("Chase", accounts.get(0).getName());
        assertEquals("Credit account with Chase Bank", accounts.get(0).getDescription());
    }

    @Test
    public void deleteAccount() throws Exception {
        database.insertAccount(sampleAccount);
        database.insertAccount(new Account("test", "description"));

        List<Account> accountsBeforeDelete = database.getAllAccounts();
        int countBeforeDelete = accountsBeforeDelete.size();

        Account accountToDelete = accountsBeforeDelete.get(0);
        database.deleteAccount(accountToDelete);

        List<Account> accountsAfterDelete = database.getAllAccounts();
        int countAfterDelete = accountsAfterDelete.size();

        assertEquals(countBeforeDelete - 1, countAfterDelete);

        ArrayList<Integer> IDsAfterDelete = new ArrayList<>();
        for (Account currentAccount : accountsAfterDelete) {
            IDsAfterDelete.add(currentAccount.getId());
        }

        assertFalse(IDsAfterDelete.contains(accountToDelete.getId()));

    }

    @Test
    public void editAccount() throws Exception {
        database.insertAccount(sampleAccount);

        List<Account> accounts = database.getAllAccounts();

        Account newAccount = new Account(sampleAccount.getName(), "new description", accounts.get(0).getId());

        database.editAccount(newAccount);

        List<Account> newAccounts = database.getAllAccounts();

        assertEquals(accounts.size(), newAccounts.size());
        assertEquals("new description", newAccounts.get(0).getDescription());
    }

    @Test
    public void insertPayee() throws Exception {
        Payee payee = new Payee("Name", "Description");

        List<Payee> payees = database.getAllPayees();
        assertEquals(0, payees.size());

        database.insertPayee(payee);

        payees = database.getAllPayees();
        assertEquals(1, payees.size());

        assertEquals("Name", payees.get(0).getName());
        assertEquals("Description", payees.get(0).getDescription());
    }

    @Test
    public void deletePayee() throws Exception {
        Payee payee = new Payee("Name", "Description");

        List<Payee> payees = database.getAllPayees();
        assertEquals(0, payees.size());

        database.insertPayee(payee);

        payees = database.getAllPayees();
        assertEquals(1, payees.size());

        database.deletePayee(payees.get(0));

        payees = database.getAllPayees();
        assertEquals(0, payees.size());
    }

    @Test
    public void editPayee() throws Exception {
        Payee payee = new Payee("Name", "Description");

        List<Payee> payees = database.getAllPayees();
        assertEquals(0, payees.size());

        database.insertPayee(payee);

        payees = database.getAllPayees();
        assertEquals(1, payees.size());

        Payee toEdit = payees.get(0);

        toEdit.setName("New Name");

        database.editPayee(toEdit);

        payees = database.getAllPayees();
        assertEquals("New Name", payees.get(0).getName());
    }

    @Test
    public void insertNote() throws Exception {
        List<Note> notesBeforeInsertion = database.getAllNotes();

        database.insertNote(this.sampleNote);

        List<Note> notesAfterInsertion = database.getAllNotes();

        assertEquals(notesBeforeInsertion.size() + 1, notesAfterInsertion.size());
    }

    @Test
    public void deleteNote() throws Exception {
        database.insertNote(this.sampleNote);

        List<Note> notesBeforeDeletion = database.getAllNotes();

        database.deleteNote(this.sampleNote);

        List<Note> notesAfterDeletion = database.getAllNotes();

        assertEquals(notesBeforeDeletion.size() - 1, notesAfterDeletion.size());
    }

    @Test
    public void editNote() throws Exception {
        Note originalNote = new Note("This is a note!");

        database.insertNote(originalNote);

        List<Note> notes = database.getAllNotes();
        Note noteToEdit = notes.get(0);

        String textToSet = "This is edited text!";
        noteToEdit.setNoteText(textToSet);

        database.editNote(noteToEdit);

        notes = database.getAllNotes();
        Note editedNote = notes.get(0);

        assertEquals(textToSet, editedNote.getNoteText());

    }

    @Test
    public void insertType() throws Exception {
        List<Type> types = database.getAllTypes();
        assertEquals(0, types.size());

        database.insertType(sampleType);

        types = database.getAllTypes();
        assertEquals(1, types.size());

        assertEquals(sampleType.getName(), types.get(0).getName());
        assertEquals(sampleType.getDescription(), types.get(0).getDescription());
    }

    @Test
    public void deleteType() throws Exception {
        List<Type> types = database.getAllTypes();
        assertEquals(0, types.size());

        database.insertType(sampleType);

        types = database.getAllTypes();
        assertEquals(1, types.size());

        database.deleteType(types.get(0));

        types = database.getAllTypes();
        assertEquals(0, types.size());
    }

    @Test
    public void editType() throws Exception {
        List<Type> types = database.getAllTypes();
        assertEquals(0, types.size());

        database.insertType(sampleType);

        types = database.getAllTypes();
        Type currentType = types.get(0);

        currentType.setName("ABCD");
        currentType.setDescription("1234");

        database.editType(currentType);

        types = database.getAllTypes();
        assertEquals(1, types.size());

        assertEquals("ABCD", types.get(0).getName());
        assertEquals("1234", types.get(0).getDescription());
    }

    @Test
    public void insertTag() throws Exception {
        List<Tag> tagsBeforeInsertion = database.getAllTags();

        database.insertTag(this.sampleTag);

        List<Tag> tagsAfterInsertion = database.getAllTags();

        assertEquals(tagsBeforeInsertion.size() + 1, tagsAfterInsertion.size());
    }

    @Test
    public void deleteTag() throws Exception {
        database.insertTag(this.sampleTag);

        List<Tag> tagsBeforeDeletion = database.getAllTags();
        Tag tagToDelete = tagsBeforeDeletion.get(0);
        database.deleteTag(tagToDelete);

        List<Tag> tagsAfterDeletion = database.getAllTags();

        assertEquals(tagsBeforeDeletion.size() - 1, tagsAfterDeletion.size());
    }

    @Test
    public void editTag() throws Exception {
        Tag originalTag = new Tag("Name", "Description");

        database.insertTag(originalTag);

        List<Tag> tags = database.getAllTags();
        Tag tagToEdit = tags.get(0);

        String textToSet = "This is edited text!";
        tagToEdit.setDescription(textToSet);

        database.editTag(tagToEdit);

        tags = database.getAllTags();
        Tag editedTag = tags.get(0);

        assertEquals(textToSet, editedTag.getDescription());
    }

    @After
    public void afterTests() throws Exception {
        database.shutdown();

        Path dbPath = Paths.get("src/test/resources/test.db");
        Files.delete(dbPath);
    }
}
