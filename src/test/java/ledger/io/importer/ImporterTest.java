package ledger.io.importer;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.database.storage.SQLiteDatabase;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tayler How on 10/11/2016.
 */
public class ImporterTest {

    private static IDatabase database;
    private static Transaction sampleTransaction1;
    private static Transaction sampleTransaction2;
    private static Transaction sampleTransaction3;
    private static Type sampleType;
    private static Account sampleAccount;
    private static Payee samplePayee;
    private static Tag sampleTag;
    private static Note sampleNote;

    @BeforeClass
    public static void setupSampleObjects() {
        sampleType = new Type("Credit", "Purchased with a credit card");
        sampleAccount = new Account("Chase", "Credit account with Chase Bank");
        samplePayee = new Payee("Meijer", "Grocery store");
        sampleTag = new Tag("Groceries", "Money spent on groceries");
        sampleNote = new Note("This is a note");

        ArrayList<Tag> sampleTagList = new ArrayList<>();
        sampleTagList.add(sampleTag);

        sampleTransaction1 = new Transaction(new Date(), sampleType, 4201, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction2 = new Transaction(new Date(), sampleType, 103, sampleAccount, samplePayee, true, sampleTagList, sampleNote);
        sampleTransaction3 = new Transaction(new Date(), sampleType, 3304, sampleAccount, samplePayee, false, sampleTagList, sampleNote);
    }
}
