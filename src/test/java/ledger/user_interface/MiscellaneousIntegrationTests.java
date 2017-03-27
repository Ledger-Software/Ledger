package ledger.user_interface;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * Tests the User Interface where the test does not fit cleanly into one of the other IntegrationTest classes
 * Uses the TestFX framework.
 */
public class MiscellaneousIntegrationTests extends FxRobot {

    /**
     * Uses the TestFx framework to export the application's data and verify that the new file exists
     */
    public void exportData() {
        clickOn("Miscellaneous");
        clickOn("#exportDataBtn");
        type(KeyCode.ENTER);
        sleep(5000);
        clickOn("#password");
        write("PasswordForUiTesting1234");
        clickOn("Continue");

        File initDir = new File(System.getProperty("user.home"));
        File[] listFiles = initDir.listFiles();
        boolean containsExportFile = false;
        for (File f : listFiles) {
            if (f.isFile() && f.getName().matches("\\d+(LedgerDB.mv.db)")) {
                containsExportFile = true;
            }
        }
        assertTrue(containsExportFile);
    }
}
