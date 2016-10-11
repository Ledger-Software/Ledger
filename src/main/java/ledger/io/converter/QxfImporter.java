package ledger.io.converter;

import ledger.database.enity.Account;
import ledger.database.enity.Transaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesse Shellabarger on 10/11/2016.
 */
public class QxfImporter implements IConverter {

    private File qxfFile;
    private Account account;

    public void QxfImporter(File file, Account account) {
        this.qxfFile = file;
        this.account = account;
    }

    @Override
    public List<Transaction> convert() {

        List<Transaction> transactionsToImport = new ArrayList();

        //modify




    }
}
