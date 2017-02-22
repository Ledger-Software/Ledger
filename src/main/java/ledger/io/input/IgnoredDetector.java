package ledger.io.input;

import ledger.database.IDatabase;
import ledger.database.entity.Transaction;
import ledger.exception.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes a list of {@link Transaction} and will sort out Ignored Transactions
 */
public class IgnoredDetector {
    private final List<Transaction> transactions;
    private final List<Transaction> verifiedTransactions;
    private final List<Transaction> ignoredTransactions;

    public IgnoredDetector(List<Transaction> transactionList){
        this.transactions = transactionList;
        this.ignoredTransactions = new ArrayList<>();
        this.verifiedTransactions = new ArrayList<>();
    }

    public IgnoredDetectionResult detectIgnoreTransactions(IDatabase database){
        for (Transaction currentTransaction:transactions) {
            try {
                if(database.isTransactionIgnored(currentTransaction))
                    this.verifiedTransactions.add(currentTransaction);
                else
                    this.ignoredTransactions.add(currentTransaction);
            } catch (StorageException e){
                e.printStackTrace();
            }
        }
        return new IgnoredDetectionResult(this.ignoredTransactions,this.verifiedTransactions);
    }
}
