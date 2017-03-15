package ledger.user_interface;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.user_interface.ui_controllers.Startup;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Login User Interface using the TestFX framework.
 */
public class LoginIntegrationTests extends ApplicationTest{

    @Override
    public void start(Stage stage) throws Exception {
        new Startup().start(stage);
    }

    public void createDatabase() {
        File initDir = new File(System.getProperty("user.home"));
        File[] listFiles = initDir.listFiles();
        boolean containsDbFile = false;
        for (File f : listFiles) {
            if (f.isFile() && f.getName().endsWith(".mv.db")) {
                containsDbFile = true;
            }
        }

        if (!containsDbFile) {
            press(KeyCode.ALT);
            press(KeyCode.TAB);

            release(KeyCode.ALT);
            release(KeyCode.TAB);

            type(KeyCode.ENTER);
        }

        clickOn("New File");
        clickOn("Select Location");
        type(KeyCode.ENTER);
        write("PasswordForUiTesting1234");
        type(KeyCode.TAB);
        write("PasswordForUiTesting1234");
        type(KeyCode.ENTER);

        press(KeyCode.ALT);
        press(KeyCode.TAB);

        release(KeyCode.ALT);
        release(KeyCode.TAB);

        type(KeyCode.ENTER);

        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.startTask();
        List<Account> accounts = task.waitForResult();

        assertEquals(0, accounts.size());
    }

    public void logout() {
        clickOn("Miscellaneous");
        clickOn("Logout");
    }
}
