package ledger.io.input;

/**
 * Created by gert on 2/7/17.
 */

import ledger.database.entity.Transaction;

import java.util.List;
/**
        * Class to store the results of IgnoredTransactionDetection. Stores two lists: one of transactions ignored
        * and one of transactions that are recognized
        */
public class IgnoredDetectionResult {

    private List<Transaction> ignoredTransactions;
    private List<Transaction> verifiedTransactions;

    public IgnoredDetectionResult(List<Transaction> ignoredTransactions, List<Transaction> verifiedTransactions) {
        this.ignoredTransactions = ignoredTransactions;
        this.verifiedTransactions = verifiedTransactions;
    }

    public List<Transaction> getIgnoredTransactions() {
        return this.ignoredTransactions;
    }

    public List<Transaction> getVerifiedTransactions() {
        return this.verifiedTransactions;
    }

}