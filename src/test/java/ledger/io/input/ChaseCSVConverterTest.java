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

public class ChaseCSVConverterTest {

    @Test(expected = ConverterException.class)
    public void throwExceptionOnMissingFile() throws Exception {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseCSVConverter converter = new ChaseCSVConverter(new File(UUID.randomUUID().toString()), testAccount);

        converter.convert();
    }

    @Test
    public void convertTest() throws Exception {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseCSVConverter converter = new ChaseCSVConverter(new File("./src/test/resources/ChaseSmallTest.csv"), testAccount);

        List<Transaction> transactionList = converter.convert();

        assertEquals(14, transactionList.size());
        transactionList.parallelStream().allMatch(t -> t.getAccount().equals(testAccount));

        int countOfPending = transactionList.parallelStream().mapToInt(t -> t.isPending() ? 1 : 0).sum();

        assertEquals(1, countOfPending);
    }

    @Test(expected = ConverterException.class)
    public void dateErrorTest() throws ConverterException {
        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseCSVConverter converter = new ChaseCSVConverter(new File("./src/test/resources/ChaseDataError.csv"), testAccount);

        converter.convert();
    }

    @Test(expected = ConverterException.class)
    public void testIOException() throws Exception {
        File csvFile = new File("./src/test/resources/ChaseSmallTest.csv");

        RandomAccessFile file = new RandomAccessFile(csvFile, "rw");
        FileLock lock = file.getChannel().lock();

        csvFile.setReadable(false);
        try {


            Account testAccount = new Account("Test Account", "Account only used for Testing");
            ChaseCSVConverter converter = new ChaseCSVConverter(csvFile, testAccount);

            converter.convert();
        } finally {
            csvFile.setReadable(true);
        }
    }
}
