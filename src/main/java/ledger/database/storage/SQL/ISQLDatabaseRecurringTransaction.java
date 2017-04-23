package ledger.database.storage.SQL;

import ledger.database.entity.RecurringTransaction;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;

/**
 * Created by Jesse Shellabarger on 4/23/2017.
 */
public interface ISQLDatabaseRecurringTransaction extends ISQLiteDatabase {

    @Override
    public void insertRecurringTrnsaction(RecurringTransaction trans) {

    }

}
