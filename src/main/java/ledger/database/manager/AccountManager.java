package ledger.database.manager;

import ledger.database.IDatabase;
import ledger.database.enity.Account;

public class AccountManager implements IManager<Account> {

    private IDatabase db;

    public void AccountManager(IDatabase db) {
        this.db = db;
    }

    @Override
    public void insert(Account account) {
        this.db.insertAccount(account);
    }

    @Override
    public void edit(Account account) {
        this.db.editAccount(account);
    }

    @Override
    public void delete(Account account) {
        this.db.deleteAccount(account);
    }
}
