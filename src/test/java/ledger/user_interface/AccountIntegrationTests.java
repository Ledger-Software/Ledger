package ledger.user_interface;


import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import org.testfx.framework.junit.ApplicationTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the User Interface regarding account interaction using the TestFX framework.
 */
public class AccountIntegrationTests extends ApplicationTest{

    @Override
    public void start(Stage stage) throws Exception {}

    public void createAccount() {
        clickOn("Account Operations");
        addAccount("Account A", "For Integration Tests", "123");
        addAccount("Account B", "For Integration Tests", "123.0");
        addAccount("Account C", "For Integration Tests", "123.10");
        addAccount("Account D", "For Integration Tests", "123.001");

        TaskWithReturn<List<Account>> accountTask = DbController.INSTANCE.getAllAccounts();
        accountTask.startTask();
        List<Account> accounts = accountTask.waitForResult();

        assertEquals(4, accounts.size());
    }

    private void addAccount(String name, String description, String amount) {
        clickOn("Add Account");
        write(name);
        type(KeyCode.TAB);
        write(description);
        type(KeyCode.TAB);
        write(amount);

        clickOn("Submit as Complete");
        sleep(100);
    }
}
