package ledger.controller;

import ledger.controller.register.Task;
import ledger.database.IDatabase;
import ledger.database.enity.Transaction;
import ledger.exception.StorageException;

import java.util.List;


/**
 * Created by gert on 10/11/16.
 */
public class DbController {

    public static DbController INSTANCE;
    private IDatabase db;
    private List<CallableMethod> DbExceptionList;
    private List<CallableMethod> DbCompleteList;
    public DbController() {
        INSTANCE = this;
    }






    public Task<Transaction, Object> insertTransaction (final Transaction transaction) throws StorageException{
        return new Task<Transaction, Object>(db::insertTransaction, transaction);

    }


}
