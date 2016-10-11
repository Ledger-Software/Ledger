package ledger.io.converter;

import ledger.database.enity.Account;
import ledger.database.enity.Transaction;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ChaseConverterTest {

    @Test(expected = FileNotFoundException.class)
    public void throwExceptionOnMissingFile() throws Exception {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseConverter converter = new ChaseConverter(new File(UUID.randomUUID().toString()), testAccount);

        converter.convert();
    }

    @Test
    public void convertTest() throws Exception {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseConverter converter = new ChaseConverter(new File("./src/test/resources/ChaseSmallTest.csv"), testAccount);

        List<Transaction> transactionList = converter.convert();


        assertEquals(14, transactionList.size());
        transactionList.parallelStream().allMatch(t -> t.getAccount().equals(testAccount));

        int countOfPending = transactionList.parallelStream().mapToInt(t -> t.isPending() ? 1:0).sum();

        assertEquals(1, countOfPending);
    }
}
