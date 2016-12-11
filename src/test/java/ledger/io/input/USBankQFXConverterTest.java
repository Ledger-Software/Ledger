package ledger.io.input;

import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for the applications FifthThirdBankQFXConverter class
 */
public class USBankQFXConverterTest {

    // Test normal operating conditions
    @Test
    public void importTransactionsTest() throws FileNotFoundException {
        File testFile = new File("src/test/resources/USBankSample.qfx");
        IInAdapter adapter = new USBankQFXConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(47, convertedTransactions.size());

        assertEquals(34940, convertedTransactions.get(0).getAmount());
        assertEquals(-10572, convertedTransactions.get(1).getAmount());
        assertEquals(-1386, convertedTransactions.get(2).getAmount());
        assertEquals(-3500, convertedTransactions.get(7).getAmount());

        assertEquals(new Date(GenerateEpoch.generate("20161201120000.000")), convertedTransactions.get(0).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20161201120000.000")), convertedTransactions.get(1).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20161201120000.000")), convertedTransactions.get(2).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20161201120000.000")), convertedTransactions.get(7).getDate());

        assertEquals("AMAZON.COM806055", convertedTransactions.get(0).getPayee().getName());
        assertEquals("INTERNET BANKING PAYMENT TO CRED", convertedTransactions.get(1).getPayee().getName());
        assertEquals("Amazon.com      AMZN.COM/BILWA", convertedTransactions.get(2).getPayee().getName());
        assertEquals("PAYPAL", convertedTransactions.get(7).getPayee().getName());

        assertEquals("Download from usbank.com. AMAZON.COM806055", convertedTransactions.get(0).getNote().getNoteText());
        assertEquals("Download from usbank.com. 1234", convertedTransactions.get(1).getNote().getNoteText());
        assertEquals("Download from usbank.com. Amazon.com      AMZN.COM/BILWA", convertedTransactions.get(2).getNote().getNoteText());
        assertEquals("Download from usbank.com.", convertedTransactions.get(7).getNote().getNoteText());

        assertEquals(TypeConversion.convert("ACH_CREDIT"), convertedTransactions.get(0).getType());
        assertEquals(TypeConversion.convert("DEBIT"), convertedTransactions.get(1).getType());
        assertEquals(TypeConversion.convert("DEBIT_CARD"), convertedTransactions.get(2).getType());
        assertEquals(TypeConversion.convert("ACH_DEBIT"), convertedTransactions.get(7).getType());
    }
}
