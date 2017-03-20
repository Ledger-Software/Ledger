package ledger.user_interface;

import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.user_interface.ui_controllers.Startup;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javax.swing.*;
import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Login User Interface using the TestFX framework.
 */
public class LoginIntegrationTests extends FxRobot {

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

        listWindows().forEach((a) -> System.out.println("WindowX: " + a.getX()));

        clickOn("#newFileBtn");
        clickOn("#saveLocationButton");
        type(KeyCode.ENTER);
        //write("PasswordForUiTesting1234");
        type(KeyCode.P);
        type(KeyCode.A);
        type(KeyCode.S);
        type(KeyCode.S);
        type(KeyCode.W);
        type(KeyCode.O);
        type(KeyCode.R);
        type(KeyCode.D);

        type(KeyCode.TAB);
        //write("PasswordForUiTesting1234");
        type(KeyCode.P);
        type(KeyCode.A);
        type(KeyCode.S);
        type(KeyCode.S);
        type(KeyCode.W);
        type(KeyCode.O);
        type(KeyCode.R);
        type(KeyCode.D);

        type(KeyCode.ENTER);


        Startup.INSTANCE.runLater(window("Welcome!")::hide);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //window("Welcome!").fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));

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
