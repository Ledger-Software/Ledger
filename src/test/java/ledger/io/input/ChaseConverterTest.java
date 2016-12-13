package ledger.io.input;

import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.exception.ConverterException;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChaseConverterTest {

    @Test(expected = ConverterException.class)
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

        int countOfPending = transactionList.parallelStream().mapToInt(t -> t.isPending() ? 1 : 0).sum();

        assertEquals(1, countOfPending);
    }
    @Test(expected = ConverterException.class)
    public void dateErrorTest() throws ConverterException {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseConverter converter = new ChaseConverter(new File("./src/test/resources/ChaseDataError.csv"), testAccount);

        converter.convert();
    }

    @Test(expected = ConverterException.class)
    public void testIOException() throws Exception {
        File csvFile = new File("./src/test/resources/ChaseSmallTest.csv");
        final RandomAccessFile raFile = new RandomAccessFile(csvFile, "rw");
        FileLock lock = null;
        try {
            lock = raFile.getChannel().lock();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to obtain file lock");
        }

        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseConverter converter = new ChaseConverter(csvFile, testAccount);

        converter.convert();

        lock.release();
    }
}
