package ledger.user_interface;

import javafx.stage.Stage;
import ledger.user_interface.ui_controllers.Startup;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;

import static org.junit.Assume.assumeTrue;

/**
 * Integration runner for our UI Integration tests. This runner will be picked up by the Junit runner.
 * Test methods in here can call methods in the various IntegrationTest classes to form an integration test.
 */
public class UiIntegrationTestsRunner extends ApplicationTest {

    private static final LoginIntegrationTests loginTests = new LoginIntegrationTests();
    private static final AccountIntegrationTests accountTests = new AccountIntegrationTests();
    private static final TransactionIntegrationTests transactionTests = new TransactionIntegrationTests();
    private static final MiscellaneousIntegrationTests miscTests = new MiscellaneousIntegrationTests();

    @Override
    public void start(Stage stage) throws Exception {
        if (!Boolean.getBoolean("headless")) {
            new Startup().start(stage);
        }
    }

    @BeforeClass
    public static void setupHeadlessRun() throws Exception {
        if ("true".equals(System.getenv("headless"))) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @Before
    public void beforeAllTests() {
        removeExistingDBFile();
        assumeTrue(!"true".equals(System.getenv("headless")));
    }

    @After
    public void afterAllTests() {
        removeExistingDBFile();
    }

    /**
     * Remove a database file with the default name, if one exists
     */
    private void removeExistingDBFile() {
        String home = System.getProperty("user.home");
        File dbFile = new File(home, "LedgerDB.mv.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testLogin() {
        loginTests.createDatabase();
        loginTests.logout(true);
    }

    @Test
    public void testCreateAccounts() {
        loginTests.createDatabase();
        accountTests.createAccounts();
        loginTests.logout(true);
    }

    @Test
    public void testDeleteAccount() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        accountTests.deleteAccount("Hello");
        loginTests.logout(true);
    }

    @Test
    public void testTransactionInsertionViaWindow() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        transactionTests.insertTransactionViaTransactionWindow();
        loginTests.logout(true);
    }

    @Ignore //This test should work once the fix for window modality is in
    @Test
    public void testInvalidValuesInTransactionWindow() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        transactionTests.insertInvalidTransactionViaTransactionWindow();
        loginTests.logout(true);
    }

    @Test
    public void testInsertTransactionViaTableView() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        transactionTests.insertTransactionViaTableView();
        loginTests.logout(true);
    }

    @Test
    public void testDeleteTransactionFromTableView() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        transactionTests.insertTransactionViaTableView();
        transactionTests.deleteTransactionViaTableView();
        loginTests.logout(true);
    }

    @Test
    public void testDataExport() {
        loginTests.createDatabase();
        miscTests.exportData();
        loginTests.logout(false);
    }
    
}
