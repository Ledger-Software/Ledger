package ledger.io.importer;

import ledger.database.IDatabase;
import ledger.database.entity.Transaction;
import ledger.exception.StorageException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tayler How on 10/11/2016.
 *
 * This class is responsible for taking lists of Java Transaction objects
 * and importing them into the backend DB.
 */
public class Importer {
    private List<Transaction> failedImports;
    private List<String> failedImportExceptionMessages;

    public Importer() {
        this.failedImports = new ArrayList<>();
        this.failedImportExceptionMessages = new ArrayList<>();
    }

    /**
     * For each transaction in transactionList attempts to put into database. Records failed imports
     * and put it into a list that can be retrieved via getFailedImports()
     * @param database IDatabase for putting the objects into
     * @param transactionList the list of transactions to import
     * @return
     */
    public boolean importTransactions(IDatabase database, List<Transaction> transactionList) {
        this.failedImports.clear();
        this.failedImportExceptionMessages.clear();

        for (Transaction currentTransaction : transactionList) {
            try {
                database.insertTransaction(currentTransaction);
            } catch (StorageException e) {
                this.failedImports.add(currentTransaction);
                this.failedImportExceptionMessages.add(e.getMessage());
            }
        }

        if (this.failedImports.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<Transaction> getFailedImports() {
        return this.failedImports;
    }

    public List<String> getFailedImportExceptionMessages() {
        return this.failedImportExceptionMessages;
    }
}
