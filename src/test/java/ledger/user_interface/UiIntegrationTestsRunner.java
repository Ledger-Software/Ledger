package ledger.user_interface;

import javafx.stage.Stage;
import ledger.database.IDatabase;
import ledger.user_interface.ui_controllers.Startup;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;

/**
 * Integration runner for our UI Integration tests. This runner will be picked up by the Junit runner.
 * Test methods in here can call methods in the various IntegrationTest classes to form an integration test.
 */
public class UiIntegrationTestsRunner extends ApplicationTest {

    private static IDatabase database;

    private static LoginIntegrationTests loginTests = new LoginIntegrationTests();
    private static AccountIntegrationTests accountTests = new AccountIntegrationTests();

    @Override
    public void start(Stage stage) throws Exception {
        new Startup().start(stage);
    }

    @BeforeClass
    public static void setupHeadlessRun() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    @Before
    @After
    public void removeExistingDBFile() {
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
    public void testCreateAccount() {
        loginTests.createDatabase();
        accountTests.createAccount();
        loginTests.logout();
    }
}
