package ledger.io.input;

import ledger.database.enity.Account;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QxfConverterTest {
    public static void main(String[] args) throws FileNotFoundException {
        //File testFile = new File("EXPORT.QXF");

        FileReader fr = new FileReader();
        System.out.print(fr.getEncoding());


        /*testFile.setReadable(true);
        System.out.println(testFile.toString());
        System.out.println(testFile.exists());
        System.out.println(testFile.canRead());
        IInAdapter adapter = new QxfConverter(testFile, new Account("test", "test"));
        try {
           // adapter.convert();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
