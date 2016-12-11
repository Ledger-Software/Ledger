package ledger.io.input;

import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.exception.ConverterException;
import ledger.exception.LedgerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QfxConverterTest {

    // Test normal operating conditions
    @Test
    public void importTransactionsTest() throws FileNotFoundException {
        File testFile = new File("src/test/resources/testQfx.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(4, convertedTransactions.size());

        assertEquals(-3309, convertedTransactions.get(0).getAmount());
        assertEquals(-2031, convertedTransactions.get(1).getAmount());
        assertEquals(-499, convertedTransactions.get(2).getAmount());
        assertEquals(-699, convertedTransactions.get(3).getAmount());

        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(0).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(1).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(2).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160919000000[-4:EDT]")), convertedTransactions.get(3).getDate());

        assertEquals("OUTBACK 1515", convertedTransactions.get(0).getPayee().getName());
        assertEquals("BIG RED LIQUORS - 011", convertedTransactions.get(1).getPayee().getName());
        assertEquals("Spotify USA", convertedTransactions.get(2).getPayee().getName());
        assertEquals("WHITE CASTLE  050057", convertedTransactions.get(3).getPayee().getName());

        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(0).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(1).getNote().getNoteText());
        assertEquals("New York     NY", convertedTransactions.get(2).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(3).getNote().getNoteText());
    }

    // Test when the body of the file gets mangled for whatever reason
    @Test(expected=ConverterException.class)
    public void importTransactionFailureMalformedQfx() throws LedgerException {
        File testFile = new File("src/test/resources/testQfxMalformedBody.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        try {
            adapter.convert();
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    // Test when the body of the file is only slightly malformed
    @Test(expected=ConverterException.class)
    public void importTransactionFailureMalformedQfx2() throws LedgerException {
        File testFile = new File("src/test/resources/testQfxMalformedBody3.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        try {
            adapter.convert();
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    // Test when the given amount value cannot be parsed
    @Test(expected=ConverterException.class)
    public void importTransactionFailureInvalidData() throws LedgerException {
        File testFile = new File("src/test/resources/testQfxInvalidData.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        try {
            adapter.convert();
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    // Test when the given Date value cannot be parsed
    @Test(expected=ConverterException.class)
    public void importTransactionFailureInvalidData2() throws LedgerException {
        File testFile = new File("src/test/resources/testQfxInvalidData2.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        try {
            adapter.convert();
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    // Test that misisng the <QFX> tag(s) has no effect.
    @Test
    public void importTransactionFailureMalformedQfxMissingOpenningQfxTag() {
        File testFile = new File("src/test/resources/testQfxMalformedBody2.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(4, convertedTransactions.size());

        assertEquals(-3309, convertedTransactions.get(0).getAmount());
        assertEquals(-2031, convertedTransactions.get(1).getAmount());
        assertEquals(-499, convertedTransactions.get(2).getAmount());
        assertEquals(-699, convertedTransactions.get(3).getAmount());

        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(0).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(1).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(2).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160919000000[-4:EDT]")), convertedTransactions.get(3).getDate());

        assertEquals("OUTBACK 1515", convertedTransactions.get(0).getPayee().getName());
        assertEquals("BIG RED LIQUORS - 011", convertedTransactions.get(1).getPayee().getName());
        assertEquals("Spotify USA", convertedTransactions.get(2).getPayee().getName());
        assertEquals("WHITE CASTLE  050057", convertedTransactions.get(3).getPayee().getName());

        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(0).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(1).getNote().getNoteText());
        assertEquals("New York     NY", convertedTransactions.get(2).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(3).getNote().getNoteText());
    }

    // Test the header is not needed/used
    @Test
    public void importTransactionFailureMalformedHeader() {
        File testFile = new File("src/test/resources/testQfxMalformedHeader.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(4, convertedTransactions.size());

        assertEquals(-3309, convertedTransactions.get(0).getAmount());
        assertEquals(-2031, convertedTransactions.get(1).getAmount());
        assertEquals(-499, convertedTransactions.get(2).getAmount());
        assertEquals(-699, convertedTransactions.get(3).getAmount());

        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(0).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(1).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160912000000[-4:EDT]")), convertedTransactions.get(2).getDate());
        assertEquals(new Date(GenerateEpoch.generate("20160919000000[-4:EDT]")), convertedTransactions.get(3).getDate());

        assertEquals("OUTBACK 1515", convertedTransactions.get(0).getPayee().getName());
        assertEquals("BIG RED LIQUORS - 011", convertedTransactions.get(1).getPayee().getName());
        assertEquals("Spotify USA", convertedTransactions.get(2).getPayee().getName());
        assertEquals("WHITE CASTLE  050057", convertedTransactions.get(3).getPayee().getName());

        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(0).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(1).getNote().getNoteText());
        assertEquals("New York     NY", convertedTransactions.get(2).getNote().getNoteText());
        assertEquals("TERRE HAUTE  IN", convertedTransactions.get(3).getNote().getNoteText());
    }
}
