package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Account;

import java.util.List;

/**
 * Allows conversion of an Account Entity both to and from a String representation.
 */
public class AccountStringConverter extends StringConverter<Account> {

    public Account fromString(String accountName) {
        if (accountName == null || accountName.isEmpty())
            return null;

        TaskWithReturn<List<Account>> getAllAccountsTask = DbController.INSTANCE.getAllAccounts();
        getAllAccountsTask.startTask();
        List<Account> allAccounts = getAllAccountsTask.waitForResult();

        for (Account currentAccount : allAccounts) {
            if (currentAccount.getName().equals(accountName)) {
                return currentAccount;
            }
        }

        return new Account(accountName, accountName);
    }

    public String toString(Account account) {
        // convert a Type instance to the text displayed in the choice box
        if (account != null) {
            return account.getName();
        } else {
            return "";
        }
    }
}
