package ledger.database.storage;

import ledger.database.entity.Payee;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Jesse Shellabarger on 11/8/2016.
 */
public class EntityTests {

    @Test
    public void testPayeeEquals() {
        Payee p1 = new Payee("name", "Description");
        Payee p2 = new Payee("name", "Description");

        assertEquals(p1, p2);

        p2.setName("Different Name");

        assertNotEquals(p1, p2);

        Payee nullPayee = new Payee(null, null);

        assertEquals(nullPayee, nullPayee);
    }
}
