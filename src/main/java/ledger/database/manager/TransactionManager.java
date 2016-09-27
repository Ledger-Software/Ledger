package ledger.database.manager;

import ledger.database.IDatabase;
import ledger.database.enity.Transaction;

public class TransactionManager implements ITimeSeries<Transaction> {

    private IDatabase db;

    public void TransactionManager(IDatabase db) {
        this.db = db;
    }

    @Override
    public void insert(Transaction transaction) {
        this.db.insertTransaction(transaction);
    }

    @Override
    public void edit(Transaction transaction) {
        this.db.editTransaction(transaction);
    }

    @Override
    public void delete(Transaction transaction) {
        this.db.deleteTransaction(transaction);
    }

}
