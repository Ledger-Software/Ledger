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

    private static LoginIntegrationTests loginTests = new LoginIntegrationTests();
    private static AccountIntegrationTests accountTests = new AccountIntegrationTests();

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
    }


    @After
    public void afterAllTests() {
        removeExistingDBFile();
    }

    public void removeExistingDBFile() {
        String home = System.getProperty("user.home");
        File dbFile = new File(home, "LedgerDB.mv.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Rule public RetryRule retryLogin = new RetryRule(2);
    @Test
    public void testLogin() {
        assumeTrue(!"true".equals(System.getenv("headless")));
        loginTests.createDatabase();
        loginTests.logout();
    }


    @Rule public RetryRule retryTestAccounts = new RetryRule(2);
    @Test
    public void testCreateAccounts() {
        assumeTrue(!"true".equals(System.getenv("headless")));

        loginTests.createDatabase();
        accountTests.createAccounts();
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
