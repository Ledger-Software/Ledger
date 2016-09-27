package ledger.database.storage;

import org.junit.Test;
import static org.junit.Assert.*;

public class SQLiteDatabaseTest {

    @Test
    public void testConstruction() {
        try {
            SQLiteDatabase database = new SQLiteDatabase(null);
        } catch (Exception e){
            fail(e.toString());
        }
    }
}
