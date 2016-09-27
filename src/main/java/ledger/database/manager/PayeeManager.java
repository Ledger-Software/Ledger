package ledger.database.manager;

import ledger.database.IDatabase;
import ledger.database.enity.Payee;

public class PayeeManager implements IManager<Payee> {

    private IDatabase db;

    public void PayeeManager(IDatabase db){
        this.db = db;
    }

    @Override
    public void insert(Payee payee) {
        this.db.insertPayee(payee);
    }

    @Override
    public void edit(Payee payee) {
        this.db.editPayee(payee);
    }

    @Override
    public void delete(Payee payee) {
        this.db.deletePayee(payee);
    }
}
