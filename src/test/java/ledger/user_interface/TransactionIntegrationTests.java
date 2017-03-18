package ledger.user_interface;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Note;
import ledger.database.entity.Transaction;
import ledger.io.input.TypeConversion;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the User Interface regarding {@link ledger.database.entity.Transaction} interaction using the TestFX framework.
 */
public class TransactionIntegrationTests extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {}

    /**
     * Uses the TestFx framework to add a {@link ledger.database.entity.Transaction} through the User Interface.
     */
    public void insertTransactions() {
        clickOn("Transaction Operations");
        clickOn("Add Transaction");

        clickOn("#payeeText");
        write("IntegrationTests");

        clickOn("#amountText");
        write("10.00");


        clickOn("#typeText");
        write("Account Credit");

        clickOn("#addTrnxnSubmitButton");

        TaskWithReturn<List<Transaction>> allTransactionsTask = DbController.INSTANCE.getAllTransactions();
        allTransactionsTask.startTask();
        List<Transaction> allTransactions = allTransactionsTask.waitForResult();

        assertEquals(1, allTransactions.size());

        Transaction t = allTransactions.get(0);

        assertEquals("IntegrationTests", t.getPayee().getName());
        assertEquals("IntegrationTests", t.getPayee().getDescription());
        assertEquals(1000, t.getAmount());
        assertEquals("Hello" , t.getAccount().getName());
        assertTrue(t.getDate().before(new Date())); //can't verify exact date, check its in the past
        assertEquals(null, t.getNote());
        assertEquals("Account Credit", t.getType().getName());
    }
}
