package ledger.user_interface;

import javafx.application.Application;
import javafx.stage.Stage;
import ledger.user_interface.ui_controllers.Startup;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Integration runner for our UI Integration tests. This runner will be picked up by the Junit runner.
 * Test methods in here can call methods in the various IntegrationTest classes to form an integration test.
 */
public class UiIntegrationTestsRunner extends FxRobot {

    private static final LoginIntegrationTests loginTests = new LoginIntegrationTests();
    private static final AccountIntegrationTests accountTests = new AccountIntegrationTests();
    private static final TransactionIntegrationTests transactionTests = new TransactionIntegrationTests();
    private static final MiscellaneousIntegrationTests miscTests = new MiscellaneousIntegrationTests();

    public static Stage primaryStage;
    private Application app;

    @BeforeClass
    public static void setUpClass() {
        if ("true".equals(System.getenv("travis"))) {
            assumeTrue(false);
        }

        try {
            // Start the Toolkit and block until the primary Stage was retrieved.
            primaryStage = FxToolkit.registerPrimaryStage();
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            fail("Timeout during stage setup");
        }
    }

    @Before
    public void beforeAllTests() {
        removeExistingDBFile();
        try {
            app = FxToolkit.setupApplication(Startup.class);

            Thread.sleep(1000);
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            fail("Timeout during application setup");
        } catch (InterruptedException ignored) { }
    }

    @After
    public void afterAllTests() {
        removeExistingDBFile();
        try {
            app.stop();
            FxToolkit.cleanupApplication(app);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
