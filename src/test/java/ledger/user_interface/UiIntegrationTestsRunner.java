package ledger.user_interface;

import javafx.stage.Stage;
import ledger.user_interface.ui_controllers.Startup;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Integration runner for our UI Integration tests. This runner will be picked up by the Junit runner.
 * Test methods in here can call methods in the various IntegrationTest classes to form an integration test.
 */
public class UiIntegrationTestsRunner extends FxRobot {

    @Rule public RetryRule retryRule = new RetryRule(1);

    private static final LoginIntegrationTests loginTests = new LoginIntegrationTests();
    private static final AccountIntegrationTests accountTests = new AccountIntegrationTests();
    private static final TransactionIntegrationTests transactionTests = new TransactionIntegrationTests();

    public static Stage primaryStage;

//    @Override
//    public void start(Stage stage) throws Exception {
//        System.out.println("Start Method");
//        if (!Boolean.getBoolean("headless")) {
//            new Startup().start(stage);
//        }
//    }

    @BeforeClass
    public static void setUpClass() {
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
            FxToolkit.setupApplication(Startup.class);

            Thread.sleep(1000);
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            fail("Timeout during application setup");
        } catch (InterruptedException ignored) { }
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
        loginTests.logout();
    }

    @Test
    public void testCreateAccounts() {
        loginTests.createDatabase();
        accountTests.createAccounts();
        loginTests.logout();
    }

    @Test
    public void testDeleteAccount() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        accountTests.deleteAccount("Hello");
        loginTests.logout();
    }

    @Test
    public void testTransactionInsertionViaWindow() {
        loginTests.createDatabase();
        accountTests.addSingleAccount("Hello", "World", "1234");
        transactionTests.insertTransactions();
        loginTests.logout();

    }

    private class RetryRule implements TestRule {
        private int retryCount;

        public RetryRule(int retryCount) {
            this.retryCount = retryCount;
        }

        public Statement apply(Statement base, Description description) {
            return statement(base, description);
        }
        private Statement statement(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Throwable caughtThrowable = null;

                    // implement retry logic here
                    for (int i = 0; i < retryCount; i++) {
                        try {
                            base.evaluate();
                            return;
                        } catch (Throwable t) {
                            caughtThrowable = t;

                            //  System.out.println(": run " + (i+1) + " failed");
                            System.err.println(description.getDisplayName() + ": run " + (i + 1) + " failed");
                        }
                    }
                    System.err.println(description.getDisplayName() + ": giving up after " + retryCount + " failures");
                    throw caughtThrowable;
                }
            };
        }
    }

}
