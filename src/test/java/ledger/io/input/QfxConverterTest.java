package ledger.io.input;

import ledger.database.enity.Account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QfxConverterTest {
    public static void main(String[] args) throws FileNotFoundException {
        File testFile = new File("src/test/resources/testQfx.QFX");
        IInAdapter adapter = new QfxConverter(testFile, new Account("test", "test"));

        try {
            adapter.convert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
