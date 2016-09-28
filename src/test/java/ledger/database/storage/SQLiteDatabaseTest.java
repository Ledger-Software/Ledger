package ledger.database.storage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SQLiteDatabaseTest {

    @Test
    public void insertTransaction() throws Exception {

    }

    @Test
    public void deleteTransaction() throws Exception {

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

    private SQLiteDatabase database;

    @Before
    public void testConstruction() throws Exception {
        database = new SQLiteDatabase(null);
    }

    @After
    public void afterTests() throws Exception {
        database.shutdown();
    }
}
