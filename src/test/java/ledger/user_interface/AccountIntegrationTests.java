package ledger.user_interface;


import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;
import ledger.database.entity.AccountBalance;
import org.testfx.framework.junit.ApplicationTest;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the User Interface regarding account interaction using the TestFX framework.
 */
public class AccountIntegrationTests extends ApplicationTest{

    @Override
    public void start(Stage stage) throws Exception {}

    /**
     * Uses the TestFx Framework to create four {@link Account} and verify that they are created with the correct
     * {@link AccountBalance}
     */
    public void createAccounts() {
        clickOn("Account Operations");
        addAccount("Account A", "For Integration Tests", "123");
        addAccount("Account B", "For Integration Tests", "123.0");
        addAccount("Account C", "For Integration Tests", "123.10");
        addAccount("Account D", "For Integration Tests", "123.001");

        TaskWithReturn<List<Account>> accountTask = DbController.INSTANCE.getAllAccounts();
        accountTask.startTask();
        List<Account> accounts = accountTask.waitForResult();

        assertEquals(4, accounts.size());

        Account accountA = null;
        Account accountB = null;
        Account accountC = null;
        Account accountD = null;

        for (Account a : accounts) {
            switch (a.getName()) {
                case "Account A": accountA = a;
                case "Account B": accountB = a;
                case "Account C": accountC = a;
                case "Account D": accountD = a;
            }
        }

        TaskWithReturn<AccountBalance> taskAccountA = DbController.INSTANCE.getBalanceForAccount(accountA);
        TaskWithReturn<AccountBalance> taskAccountB = DbController.INSTANCE.getBalanceForAccount(accountB);
        TaskWithReturn<AccountBalance> taskAccountC = DbController.INSTANCE.getBalanceForAccount(accountC);
        TaskWithReturn<AccountBalance> taskAccountD = DbController.INSTANCE.getBalanceForAccount(accountD);

        taskAccountA.startTask();
        AccountBalance balanceA = taskAccountA.waitForResult();

        taskAccountB.startTask();
        AccountBalance balanceB = taskAccountB.waitForResult();

        taskAccountC.startTask();
        AccountBalance balanceC = taskAccountC.waitForResult();

        taskAccountD.startTask();
        AccountBalance balanceD = taskAccountD.waitForResult();

        assertEquals(12300, balanceA.getAmount());
        assertEquals(12300, balanceB.getAmount());
        assertEquals(12310, balanceC.getAmount());
        assertEquals(12300, balanceD.getAmount());
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

    /**
     * Uses the TestFx framework to add a single {@link Account}
     *
     * @param accountName Name of the account
     * @param accountDescription Description of the account
     * @param amount Account balance
     */
    public void addSingleAccount(String accountName, String accountDescription, String amount) {
        clickOn("Account Operations");
        addAccount(accountName, accountDescription, amount);
    }

    /**
     * Uses the TestFx framework to delete an {@link Account} through the user interface and verify that that the changes
     * persist to the backend database.
     *
     * @param accountName Name of the account to delete
     */
    public void deleteAccount(String accountName) {
        TaskWithReturn<List<Account>> accountsTask = DbController.INSTANCE.getAllAccounts();
        accountsTask.startTask();
        List<Account> accounts = accountsTask.waitForResult();
        int numAccountsBeforeDelete = accounts.size();

        clickOn(accountName);
        clickOn("Delete Account");

        //Clear popup warning about transaction deletion
        press(KeyCode.ALT);
        press(KeyCode.TAB);

        release(KeyCode.ALT);
        release(KeyCode.TAB);

        type(KeyCode.ENTER);

        accountsTask = DbController.INSTANCE.getAllAccounts();
        accountsTask.startTask();
        accounts = accountsTask.waitForResult();
        int numAccountsAfterDelete = accounts.size();

        assertEquals(numAccountsBeforeDelete - 1, numAccountsAfterDelete);

    }
}
