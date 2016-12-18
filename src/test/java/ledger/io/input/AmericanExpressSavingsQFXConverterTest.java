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
public class AmericanExpressSavingsQFXConverterTest {

    // Test normal operating conditions
    @Test
    public void importTransactionsTest() throws FileNotFoundException {
        File testFile = new File("src/test/resources/AmericanExpressSavingsSample.qfx");
        IInAdapter adapter = new AmericanExpressSavingsQFXConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(9, convertedTransactions.size());

        assertEquals(-1900, convertedTransactions.get(0).getAmount());
        assertEquals(44280, convertedTransactions.get(1).getAmount());

        assertEquals(new Date(GenerateEpoch.generate("20161205003436")), convertedTransactions.get(0).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20161205003436")), convertedTransactions.get(1).getDate());

        assertEquals("***Not Available***", convertedTransactions.get(0).getPayee().getName());
        assertEquals("***Not Available***", convertedTransactions.get(1).getPayee().getName());

        assertEquals("Imported from American Express Savings QFX (does not include Payee info)", convertedTransactions.get(0).getNote().getNoteText());
        assertEquals("Imported from American Express Savings QFX (does not include Payee info)", convertedTransactions.get(1).getNote().getNoteText());

        assertEquals(TypeConversion.convert("DEBIT"), convertedTransactions.get(0).getType());
        assertEquals(TypeConversion.convert("CREDIT"), convertedTransactions.get(1).getType());
    }
}
