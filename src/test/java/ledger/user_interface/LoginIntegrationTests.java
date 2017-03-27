package ledger.user_interface;

import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.user_interface.ui_controllers.Startup;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Login User Interface using the TestFX framework.
 */
public class LoginIntegrationTests extends FxRobot {

    public void createDatabase() {
        try{
            clickOn(window("Hello!"), MouseButton.PRIMARY);
            type(KeyCode.ENTER);
        } catch (NoSuchElementException ignored){}

        clickOn("#newFileBtn");
        clickOn("#saveLocationButton");
        type(KeyCode.ENTER);
        sleep(1000);
        write("PasswordForUiTesting1234");
        type(KeyCode.TAB);
        write("PasswordForUiTesting1234");
        type(KeyCode.ENTER);

        try {
            clickOn(window("Welcome!"), MouseButton.PRIMARY);
            type(KeyCode.ENTER);
        } catch (NoSuchElementException ignored){}

        TaskWithReturn<List<Account>> task = DbController.INSTANCE.getAllAccounts();
        task.startTask();
        List<Account> accounts = task.waitForResult();

        assertEquals(0, accounts.size());
    }

    /**
     * Uses the TestFx framework to logout from the application, when the Miscellaneous VBox is currently closed
     *
     * @param vBoxClosed True if the Miscellaneous VBox is currently closed
     */
    public void logout(boolean vBoxClosed) {
        clickOn("Account Operations");
        scroll(50, VerticalDirection.DOWN);
        if (vBoxClosed) clickOn("Miscellaneous");
        scroll(20, VerticalDirection.DOWN);
        clickOn("Logout");
    }
}
