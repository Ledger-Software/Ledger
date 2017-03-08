package ledger.io.input;

import ledger.database.entity.Transaction;

import java.util.List;

/**
 * Class to store the results of IgnoredTransactionDetection. Stores two lists: one of transactions ignored
 * and one of transactions that are recognized
 */
public class IgnoredDetectionResult {

    private final List<Transaction> ignoredTransactions;
    private final List<Transaction> verifiedTransactions;

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