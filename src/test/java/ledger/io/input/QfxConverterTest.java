package ledger.io.input;

import ledger.database.enity.Account;
import ledger.database.enity.Transaction;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QfxConverterTest {

    @Test
    public void importTransactionsTest() throws FileNotFoundException {
        File testFile = new File("src/test/resources/testQfx.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        List<Transaction> convertedTransactions = null;
        try {
            convertedTransactions = adapter.convert();
        } catch (IOException e) {
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
