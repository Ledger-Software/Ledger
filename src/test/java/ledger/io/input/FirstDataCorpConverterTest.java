package ledger.io.input;

import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.exception.ConverterException;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the applications PayPalCSVConverter class
 */
public class FirstDataCorpConverterTest {

    private Account testAccount = new Account("Test Account", "Account only used for Testing");

    @Test(expected = ConverterException.class)
    public void throwExceptionOnMissingFile() throws Exception {
        IInAdapter<Transaction> converter = new FirstDataCorpCSVConverter(new File(UUID.randomUUID().toString()), testAccount);

        converter.convert();
    }

    @Test
    public void convertTest() throws Exception {
        IInAdapter<Transaction> converter = new FirstDataCorpCSVConverter(new File("./src/test/resources/FirstDataCorpTest.csv"), testAccount);

        List<Transaction> transactionList = converter.convert();

        assertEquals(5, transactionList.size());
        transactionList.parallelStream().allMatch(t -> t.getAccount().equals(testAccount));

        int countOfPending = transactionList.parallelStream().mapToInt(t -> t.isPending() ? 1 : 0).sum();

        assertEquals(0, countOfPending);
    }

    @Test(expected = ConverterException.class)
    public void dateErrorTest() throws ConverterException {
        IInAdapter<Transaction> converter = new FirstDataCorpCSVConverter(new File("./src/test/resources/FirstDataCorpTestDataError.csv"), testAccount);

        converter.convert();
    }

    @Test(expected = ConverterException.class)
    public void testIOException() throws Exception {
        File csvFile = new File("./src/test/resources/FirstDataCorpTest.csv");

        RandomAccessFile file = new RandomAccessFile(csvFile, "rw");
        file.getChannel().lock();

        csvFile.setReadable(false);
        try {
            IInAdapter<Transaction> converter = new FirstDataCorpCSVConverter(csvFile, testAccount);

            converter.convert();
        } finally {
            csvFile.setReadable(true);
        }
    }
}
