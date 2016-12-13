package ledger.io.input;

import ledger.controller.ImportController;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.exception.ConverterException;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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
        raFile.getChannel().lock();

        Account testAccount = new Account("Test Account", "Account only used for Testing");
        ChaseConverter converter = new ChaseConverter(csvFile, testAccount);

        converter.convert();
    }
}
