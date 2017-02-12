package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.entity.*;
import ledger.database.storage.SQL.H2.H2Database;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class H2DatabaseTest {

    private static IDatabase database;
    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Type sampleType;
    private static Type sampleType2;
    private static Account sampleAccount;
    private static Payee samplePayee;
    private static Payee samplePayee2;
    private static Payee samplePayee3;
    private static Tag sampleTag;
    private static Tag sampleTag2;
    private static Note sampleNote;
    private static Note sampleNote2;
    private static Note sampleNote3;
    private static AccountBalance sampleBalance;
    private static IgnoredExpression sampleIgnoreMatchMejer;
    private static IgnoredExpression sampleIgnoreContainEr;

    @BeforeClass
    public static void setupDatabase() throws Exception {
        database = new H2Database("./src/test/resources/testH2", "password");
    }

    @Before
    public void setupTestData() throws Exception {
        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleType2 = new Type("Debit", "Purchased with a debit card");
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        samplePayee2 = new Payee("Kroger", "Grocery store");
        samplePayee3 = new Payee("Wal-Mart", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleTag2 = new Tag("Electronics", "Money spent on electronics");
        sampleNote = new Note("This is a note");
        sampleNote2 = new Note("This is also a note");
        sampleNote3 = new Note("This is also a note, ditto");
        sampleIgnoreMatchMejer = new IgnoredExpression("Meijer", true);
        sampleIgnoreContainEr = new IgnoredExpression("er", false);
        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        sampleTransaction1 = new Transaction(new Date(), sampleType, 4201, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction2 = new Transaction(new Date(), sampleType, 103, sampleAccount, samplePayee, true, sampleTagList, sampleNote2);
        sampleTransaction3 = new Transaction(new Date(), sampleType, 3304, sampleAccount, samplePayee, false, sampleTagList, sampleNote3);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2011, Calendar.OCTOBER, 1);
        long secondsSinceEpoch = calendar.getTimeInMillis() / 1000L;

        sampleBalance = new AccountBalance(sampleAccount, new Date(secondsSinceEpoch), 1000);

        database.insertType(sampleType);
        database.insertType(sampleType2);
    }

    @Test
    public void insertTransaction() throws Exception {
        int sizeBefore = database.getAllTransactions().size();

        database.insertTransaction(sampleTransaction1);

        List<Transaction> trans = database.getAllTransactions();

        Transaction insertedTransaction = null;
        for (Transaction t : trans) {
            if (t.getId() == sampleTransaction1.getId()) insertedTransaction = t;
        }

        if (insertedTransaction == null) fail();

        assertEquals(sizeBefore + 1, trans.size());
        assertEquals(sampleTransaction1.getAmount(), insertedTransaction.getAmount());
        assertEquals(sampleTransaction1.isPending(), insertedTransaction.isPending());
        assertEquals(sampleTransaction1.getNote().getNoteText(), insertedTransaction.getNote().getNoteText());

        // Check that IDs are written back to java objects
        assertEquals(insertedTransaction.getAccount().getId(), sampleTransaction1.getAccount().getId());
        assertEquals(insertedTransaction.getPayee().getId(), sampleTransaction1.getPayee().getId());
        assertEquals(insertedTransaction.getType().getId(), sampleTransaction1.getType().getId());
        for (int i = 0; i < insertedTransaction.getTags().size(); i++) {
            assertEquals(insertedTransaction.getTags().get(i).getId(), sampleTransaction1.getTags().get(i).getId());
        }
    }

    @Test
    public void deleteTransaction() throws Exception {
        database.insertTransaction(sampleTransaction1);
        database.insertTransaction(sampleTransaction2);
        database.insertTransaction(sampleTransaction3);

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);
        Transaction randomTrans = new Transaction(new Date(), sampleType2, 214, sampleAccount, samplePayee, false, sampleTagList, sampleNote3);
        database.insertTransaction(randomTrans);

        List<Transaction> transactionsBeforeDelete = database.getAllTransactions();
        int countBeforeDelete = transactionsBeforeDelete.size();

        Transaction transactionToDelete = randomTrans;
        database.deleteTransaction(transactionToDelete);

        List<Transaction> transactionsAfterDelete = database.getAllTransactions();
        int countAfterDelete = transactionsAfterDelete.size();

        assertEquals(countBeforeDelete - 1, countAfterDelete);

        ArrayList<Integer> IDsAfterDelete = transactionsAfterDelete.stream()
                .map(Transaction::getId).collect(Collectors.toCollection(ArrayList::new));

        assertFalse(IDsAfterDelete.contains(transactionToDelete.getId()));
    }

    @Test
    public void editTransaction() throws Exception {
        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        Transaction originalTransaction = new Transaction(new Date(), sampleType, 1202, sampleAccount, samplePayee,
                false, sampleTagList, sampleNote);

        database.insertTransaction(originalTransaction);
        int idBefore = originalTransaction.getId();

        int amountToSet = 99999;
        originalTransaction.setAmount(amountToSet);
        database.editTransaction(originalTransaction);

        List<Transaction> transactions = database.getAllTransactions();

        Transaction editedTransaction = null;

        for (Transaction t : transactions) {
            if (t.getId() == originalTransaction.getId()) editedTransaction = t;
        }

        if (editedTransaction == null) fail();

        assertEquals(amountToSet, editedTransaction.getAmount());
        assertEquals(idBefore, editedTransaction.getId());
    }

    @Test
    public void getAllTransactions() throws Exception {
        int sizeBefore = database.getAllTransactions().size();

        database.insertTransaction(sampleTransaction1);
        database.insertTransaction(sampleTransaction2);
        database.insertTransaction(sampleTransaction3);

        List<Transaction> trans = database.getAllTransactions();

        assertEquals(sizeBefore + 3, trans.size());

        //pull out ids
        List<Integer> ids = trans.stream().map(Transaction::getId).collect(Collectors.toList());

        assertTrue(ids.contains(sampleTransaction1.getId()));
        assertTrue(ids.contains(sampleTransaction2.getId()));
        assertTrue(ids.contains(sampleTransaction3.getId()));
    }

    @Test
    public void insertAccount() throws Exception {
        int sizeBefore = database.getAllAccounts().size();

        database.insertAccount(sampleAccount);

        List<Account> accounts = database.getAllAccounts();
        assertEquals(sizeBefore + 1, accounts.size());

        Account insertedAccount = null;
        for (Account a : accounts) {
            if (a.getId() == sampleAccount.getId()) insertedAccount = a;
        }

        if (insertedAccount == null) fail(); //wasn't in db for whatever reason

        assertEquals(sampleAccount.getId(), insertedAccount.getId());
        assertEquals("Chase", insertedAccount.getName());
        assertEquals("Credit account with Chase Bank", insertedAccount.getDescription());
    }

    @Test
    public void deleteAccount() throws Exception {
        database.insertAccount(sampleAccount);
        Account randomAcc = new Account("test", "description");
        database.insertAccount(randomAcc);

        List<Account> accountsBeforeDelete = database.getAllAccounts();
        int countBeforeDelete = accountsBeforeDelete.size();

        int deletedId = randomAcc.getId();
        database.deleteAccount(randomAcc);

        List<Account> accountsAfterDelete = database.getAllAccounts();
        int countAfterDelete = accountsAfterDelete.size();

        assertEquals(countBeforeDelete - 1, countAfterDelete);

        ArrayList<Integer> IDsAfterDelete = new ArrayList<>();
        for (Account currentAccount : accountsAfterDelete) {
            IDsAfterDelete.add(currentAccount.getId());
        }

        assertFalse(IDsAfterDelete.contains(deletedId));
    }

    @Test
    public void editAccount() throws Exception {
        database.insertAccount(sampleAccount);

        List<Account> accounts = database.getAllAccounts();

        sampleAccount.setDescription("New Description");

        database.editAccount(sampleAccount);

        List<Account> newAccounts = database.getAllAccounts();

        Account editedAccount = null;

        for (Account a : newAccounts) {
            if (a.getId() == sampleAccount.getId()) editedAccount = a;
        }

        if (editedAccount == null) fail(); // ID was no longer in DB

        assertEquals(sampleAccount.getId(), editedAccount.getId());
        assertEquals(accounts.size(), newAccounts.size());
        assertEquals("New Description", editedAccount.getDescription());
    }

    @Test
    public void insertPayee() throws Exception {
        int sizeBefore = database.getAllPayees().size();

        Payee testPayee = new Payee("test", "");
        database.insertPayee(testPayee);

        List<Payee> payees = database.getAllPayees();
        assertEquals(sizeBefore + 1, payees.size());

        Payee insertedPayee = null;

        for (Payee p : payees) {
            if (p.getId() == testPayee.getId()) insertedPayee = p;
        }

        if (insertedPayee == null) fail();

        assertEquals(testPayee.getId(), insertedPayee.getId());
        assertEquals(testPayee.getName(), insertedPayee.getName());
        assertEquals(testPayee.getDescription(), insertedPayee.getDescription());
    }

    @Test
    public void insertPayeeDuplicate() throws Exception {
        int sizeBefore = database.getAllPayees().size();

        Payee testPayee = new Payee("testInsertPayeeDupes", "");
        database.insertPayee(testPayee);
        database.insertPayee(testPayee);

        List<Payee> payees = database.getAllPayees();
        assertEquals(sizeBefore + 1, payees.size());

        Payee insertedPayee = null;

        for (Payee p : payees) {
            if (p.getId() == testPayee.getId()) insertedPayee = p;
        }

        if (insertedPayee == null) fail();

        assertEquals(testPayee.getId(), insertedPayee.getId());
        assertEquals(testPayee.getName(), insertedPayee.getName());
        assertEquals(testPayee.getDescription(), insertedPayee.getDescription());
    }

    @Test
    public void deletePayee() throws Exception {
        int sizeBefore = database.getAllPayees().size();

        database.insertPayee(samplePayee);

        List<Payee> payees = database.getAllPayees();
        assertEquals(sizeBefore + 1, payees.size());

        database.deletePayee(samplePayee);

        payees = database.getAllPayees();
        assertEquals(sizeBefore, payees.size());

        List<Integer> ids = payees.stream().map(Payee::getId).collect(Collectors.toList());

        assertFalse(ids.contains(samplePayee.getId()));
    }

    @Test
    public void editPayee() throws Exception {
        database.insertPayee(samplePayee);

        List<Payee> payees = database.getAllPayees();
        Payee toEdit = payees.get(0);
        int originalId = toEdit.getId();

        toEdit.setName("New Name");
        database.editPayee(toEdit);

        payees = database.getAllPayees();

        Payee editedPayee = null;

        for (Payee p : payees) {
            if (p.getId() == originalId) editedPayee = p;
        }
        if (editedPayee == null) fail();

        assertEquals("New Name", editedPayee.getName());
        assertEquals(originalId, editedPayee.getId());
    }

    @Test
    public void insertNote() throws Exception {
        int sizeBefore = database.getAllNotes().size();

        database.insertTransaction(sampleTransaction3);
        sampleNote3.setTransactionId(sampleTransaction3.getId());
        List<Note> notesAfterInsertion = database.getAllNotes();

        assertEquals(sizeBefore + 1, notesAfterInsertion.size());
        Note insertedNote = null;

        for (Note n : notesAfterInsertion) {
            if (n.getNoteText().equals(sampleNote3.getNoteText()) && n.getTransactionId() == sampleNote3.getTransactionId())
                insertedNote = n;
        }

        assertNotNull(insertedNote); //Already tested all fields as part of the search...
    }

    @Test
    public void deleteNote() throws Exception {
        Note testNote = new Note(5678, "This is a test note");
        this.sampleTransaction3.setNote(testNote);
        database.insertTransaction(this.sampleTransaction3);

        List<Note> notesBeforeDeletion = database.getAllNotes();

        database.deleteNote(testNote);

        List<Note> notesAfterDeletion = database.getAllNotes();

        assertEquals(notesBeforeDeletion.size() - 1, notesAfterDeletion.size());

        List<Integer> transIds = notesAfterDeletion.stream().map(Note::getTransactionId).collect(Collectors.toList());

        assertFalse(transIds.contains(testNote.getTransactionId()));
    }

    @Test
    public void editNote() throws Exception {
        Note testNote = new Note(1234, "A note to test");
        this.sampleTransaction2.setNote(testNote);
        database.insertTransaction(sampleTransaction2);

        List<Note> notes = database.getAllNotes();
        Note noteToEdit = testNote;

        int originalTransactionId = noteToEdit.getTransactionId();
        String textToSet = "This is edited text!";
        noteToEdit.setNoteText(textToSet);

        database.editNote(noteToEdit);

        notes = database.getAllNotes();

        Note editedNote = null;
        for (Note n : notes) {
            if (n.getNoteText().equals(noteToEdit.getNoteText()) && n.getTransactionId() == originalTransactionId) {
                editedNote = n;
            }
        }

        assertNotNull(editedNote); //already tested all fields in the loop above
    }

    @Test
    public void insertType() throws Exception {
        int sizeBeforeInsert = database.getAllTypes().size();

        database.insertType(sampleType);

        List<Type> typesAfterInsert = database.getAllTypes();
        assertEquals(sizeBeforeInsert + 1, typesAfterInsert.size());

        Type insertedType = null;
        for (Type t : typesAfterInsert) {
            if (t.getId() == sampleType.getId()) insertedType = t;
        }
        if (insertedType == null) fail();

        assertEquals(sampleType.getId(), insertedType.getId());
        assertEquals(sampleType.getName(), insertedType.getName());
        assertEquals(sampleType.getDescription(), insertedType.getDescription());
    }

    @Test
    public void deleteType() throws Exception {
        Type testType3 = new Type("Testing Delete", "Used to test delete");
        Type testType4 = new Type("Testing Delete2", "Also being used to test delete");

        database.insertType(testType3);
        database.insertType(testType4);

        List<Type> typesBeforeDelete = database.getAllTypes();
        int sizeBeforeDelete = typesBeforeDelete.size();

        int deletedId = testType3.getId();
        database.deleteType(testType3);

        List<Type> typesAfterDelete = database.getAllTypes();
        assertEquals(sizeBeforeDelete - 1, typesAfterDelete.size());

        List<Integer> idsAfterDelete = typesAfterDelete.stream().map(Type::getId).collect(Collectors.toList());

        assertFalse(idsAfterDelete.contains(deletedId));
    }

    @Test
    public void editType() throws Exception {
        database.insertType(sampleType);

        List<Type> types = database.getAllTypes();
        int sizeBeforeEdit = types.size();

        Type typeToEdit = types.get(0);
        int idBeforeEdit = typeToEdit.getId();

        typeToEdit.setName("ABCD");
        typeToEdit.setDescription("1234");

        database.editType(typeToEdit);

        types = database.getAllTypes();
        assertEquals(sizeBeforeEdit, types.size());

        Type editedType = null;
        for (Type t : types) {
            if (t.getId() == idBeforeEdit) editedType = t;
        }
        if (editedType == null) fail();

        assertEquals(idBeforeEdit, editedType.getId());
        assertEquals("ABCD", editedType.getName());
        assertEquals("1234", editedType.getDescription());
    }

    @Test
    public void insertTag() throws Exception {
        List<Tag> tagsBeforeInsertion = database.getAllTags();

        Tag testTag = new Tag("testInsertTag", "");
        database.insertTag(testTag);

        List<Tag> tagsAfterInsertion = database.getAllTags();

        Tag insertedTag = null;
        for (Tag t : tagsAfterInsertion) {
            if (t.getId() == testTag.getId()) insertedTag = t;
        }
        if (insertedTag == null) fail();

        assertEquals(tagsBeforeInsertion.size() + 1, tagsAfterInsertion.size());
        assertEquals(testTag.getId(), insertedTag.getId());
        assertEquals(testTag.getName(), insertedTag.getName());
    }

    @Test
    public void insertTagDuplicate() throws Exception {
        List<Tag> tagsBeforeInsertion = database.getAllTags();

        Tag testTag = new Tag("testInsertTagDupes", "");
        database.insertTag(testTag);
        database.insertTag(testTag);

        List<Tag> tagsAfterInsertion = database.getAllTags();

        Tag insertedTag = null;
        for (Tag t : tagsAfterInsertion) {
            if (t.getId() == testTag.getId()) insertedTag = t;
        }
        if (insertedTag == null) fail();

        assertEquals(tagsBeforeInsertion.size() + 1, tagsAfterInsertion.size());
        assertEquals(testTag.getId(), insertedTag.getId());
        assertEquals(testTag.getName(), insertedTag.getName());
    }

    @Test
    public void deleteTag() throws Exception {
        database.insertTag(sampleTag);
        database.insertTag(sampleTag2); //make sure there are some tags in the db

        List<Tag> tagsBeforeDeletion = database.getAllTags();
        database.deleteTag(sampleTag);

        List<Tag> tagsAfterDeletion = database.getAllTags();

        //pull out ids
        List<Integer> ids = tagsAfterDeletion.stream().map(Tag::getId).collect(Collectors.toList());

        assertEquals(tagsBeforeDeletion.size() - 1, tagsAfterDeletion.size());
        assertFalse(ids.contains(sampleTag.getId()));
    }

    @Test
    public void editTag() throws Exception {
        database.insertTag(sampleTag2);

        List<Tag> tags = database.getAllTags();
        Tag tagToEdit = tags.get(0);

        String textToSet = "This is edited text!";
        tagToEdit.setDescription(textToSet);

        database.editTag(tagToEdit);

        tags = database.getAllTags();
        Tag editedTag = null;
        for (Tag t : tags) {
            if (t.getId() == tagToEdit.getId()) editedTag = t;
        }
        if (editedTag == null) fail();

        assertEquals(textToSet, editedTag.getDescription());
        assertEquals(tagToEdit.getId(), editedTag.getId());
    }

    @Test
    public void addTagForPayee() throws Exception {
        database.addTagForPayee(sampleTag, samplePayee);

        List<Tag> tags = database.getAllTagsForPayee(samplePayee);

        assertEquals(1, tags.size());
        assertEquals(tags.get(0).getName(), "Groceries");

        database.addTagForPayee(sampleTag2, samplePayee);
        tags = database.getAllTagsForPayee(samplePayee);
        assertEquals(2, tags.size());
    }

    @Test
    public void deleteTagForPayee() throws Exception {
        database.addTagForPayee(sampleTag, samplePayee);
        database.addTagForPayee(sampleTag2, samplePayee);
        List<Tag> tags = database.getAllTagsForPayee(samplePayee);

        assertEquals(2, tags.size());

        database.deleteTagForPayee(sampleTag2, samplePayee);
        tags = database.getAllTagsForPayee(samplePayee);
        assertEquals(1, tags.size());
        assertEquals(tags.get(0).getName(), "Groceries");
        database.deleteTagForPayee(sampleTag, samplePayee);
        tags = database.getAllTagsForPayee(samplePayee);
        assertEquals(0, tags.size());
    }

    @Test
    public void deleteAllTagsForPayeeTest() throws Exception {
        database.insertPayee(samplePayee2);
        database.addTagForPayee(sampleTag, samplePayee2);
        database.addTagForPayee(sampleTag2, samplePayee2);

        database.deleteAllTagsForPayee(samplePayee2);
        int expected = 0;
        int actual = database.getAllTagsForPayee(samplePayee2).size();

        assertEquals(expected, actual);
    }

    @Test
    public void accountBalanceTest() throws Exception {
        database.insertAccount(sampleAccount);
        database.addBalanceForAccount(sampleBalance);

        AccountBalance returned = database.getBalanceForAccount(sampleAccount);
        assertEquals(sampleBalance, returned);
    }

    @Test
    public void insertIgnoreTest() throws Exception {
        int sizebefore = database.getAllIgnoredExpressions().size();
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore);
        database.insertIgnoredExpression(sampleIgnoreContainEr);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 1);
        database.insertIgnoredExpression(sampleIgnoreMatchMejer);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 2);
    }

    @Test
    public void editIgnoreTest() throws Exception {
        database.insertIgnoredExpression(sampleIgnoreContainEr);
        assertEquals(database.getAllIgnoredExpressions().get(0).isMatch(), false);
        sampleIgnoreContainEr.setExpressionId(database.getAllIgnoredExpressions().get(0).getExpressionId());
        sampleIgnoreContainEr.setMatch(true);
        database.editIgnoredExpression(sampleIgnoreContainEr);
        assertEquals(database.getAllIgnoredExpressions().get(0).isMatch(), true);

    }

    @Test
    public void deleteIgnoreTest() throws Exception {
        int sizebefore = database.getAllIgnoredExpressions().size();
        database.insertIgnoredExpression(sampleIgnoreContainEr);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 1);
        database.insertIgnoredExpression(sampleIgnoreMatchMejer);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 2);
        database.deleteIgnoredExpression(database.getAllIgnoredExpressions().get(0));
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 1);
        database.deleteIgnoredExpression(database.getAllIgnoredExpressions().get(0));
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore);
    }

    @Test
    public void checkIgnoreTransactionTest() throws Exception {
        int sizebefore = database.getAllIgnoredExpressions().size();
        database.insertIgnoredExpression(sampleIgnoreContainEr);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 1);
        database.insertIgnoredExpression(sampleIgnoreMatchMejer);
        assertEquals(database.getAllIgnoredExpressions().size(), sizebefore + 2);
        assertEquals(database.isTransactionIgnored(sampleTransaction1), false);
        sampleTransaction1.setPayee(samplePayee2);
        assertEquals(database.isTransactionIgnored(sampleTransaction1), false);
        sampleTransaction1.setPayee(samplePayee3);
        assertEquals(database.isTransactionIgnored(sampleTransaction1), true);

    }


    @AfterClass
    public static void afterTests() throws Exception {
        database.shutdown();

        Path dbPath = Paths.get("src/test/resources/testH2.mv.db");
        Files.delete(dbPath);
    }
}
