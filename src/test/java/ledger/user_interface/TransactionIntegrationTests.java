package ledger.user_interface;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;
import ledger.io.input.TypeConversion;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the User Interface regarding {@link ledger.database.entity.Transaction} interaction using the TestFX framework.
 */
public class TransactionIntegrationTests extends FxRobot {

    /**
     * Uses the TestFx framework to add a {@link ledger.database.entity.Transaction} through the User Interface.
     */
    public void insertTransactionViaTransactionWindow() {
        clickOn("Transaction Operations");
        clickOn("#addTransactionBtn");

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
        //We don't know exactly when the transaction was imported. Best we can do is verify its in the past
        assertTrue(t.getDate().before(new Date()));
        assertEquals(null, t.getNote());
        assertEquals("Account Credit", t.getType().getName());
    }

    /**
     * Uses the TestFx framework to import a {@link Transaction} and verify correctness.
     */
//    public void editTransactionViaTableView() {
//        doubleClickOn("Cleared");
//        clickOn("Pending");
//
//        doubleClickOn("Account Credit");
//        clickOn("Cash");
//
//        TaskWithReturn<List<Transaction>> allTransactionsTask = DbController.INSTANCE.getAllTransactions();
//        allTransactionsTask.startTask();
//        List<Transaction> allTransactions = allTransactionsTask.waitForResult();
//
//        assertEquals(1, allTransactions.size());
//
//        Transaction trans = allTransactions.get(0);
//        assertEquals(TypeConversion.convert("Cash"), trans.getType());
//
//        assertTrue(!trans.isPending());
//    }

    /**
     * Uses the TestFx framework to import an invalid {@link Transaction} and verify that the errors are caught.
     */
    public void insertInvalidTransactionViaTransactionWindow() {
        clickOn("Transaction Operations");
        clickOn("#addTransactionBtn");

        clickOn("#addTrnxnSubmitButton");

        this.altTabThenEnter();

        clickOn("#payeeText");
        write("IntegrationTests");

        clickOn("#addTrnxnSubmitButton");

        this.altTabThenEnter();

        clickOn("#amountText");
        write("10.00");

        clickOn("#addTrnxnSubmitButton");

        this.altTabThenEnter();

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
        //We don't know exactly when the transaction was imported. Best we can do is verify its in the past
        assertTrue(t.getDate().before(new Date()));
        assertEquals(null, t.getNote());
        assertEquals("Account Credit", t.getType().getName());
    }

    /**
     * Uses the TestFx framework to verify insertion of a {@link Transaction} through the
     * {@link ledger.user_interface.ui_controllers.component.TransactionTableView}
     */
    public void insertTransactionViaTableView(){
        rightClickOn("#transactionTableView");
        clickOn("Add Transaction");

        TaskWithReturn<List<Transaction>> allTransactionsTask = DbController.INSTANCE.getAllTransactions();
        allTransactionsTask.startTask();
        List<Transaction> allTransactions = allTransactionsTask.waitForResult();

        assertEquals(1, allTransactions.size());

        Transaction t = allTransactions.get(0);

        assertEquals(new Payee("",""), t.getPayee());
        assertEquals(0, t.getAmount());
        assertTrue(t.isPending());
        assertEquals(null, t.getNote());
        assertEquals(null, t.getNote());
        assertEquals(TypeConversion.convert("UNKNOWN"), t.getType());
    }

    public void deleteTransactionViaTableView() {
        clickOn("UNKNOWN");
        rightClickOn("#transactionTableView");
        clickOn("Delete Selected Transaction(s)");
        press(KeyCode.ENTER);

        TaskWithReturn<List<Transaction>> allTransactionsTask = DbController.INSTANCE.getAllTransactions();
        allTransactionsTask.startTask();
        List<Transaction> allTransactions = allTransactionsTask.waitForResult();

        assertEquals(0, allTransactions.size());
    }

    private void altTabThenEnter() {
        press(KeyCode.ALT);
        press(KeyCode.TAB);
        release(KeyCode.ALT);
        release(KeyCode.TAB);
        press(KeyCode.ENTER);
    }
}
