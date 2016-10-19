package ledger.io.input;

import ledger.database.entity.Transaction;

import java.util.List;

/**
 * Class to store the results of DuplicateDetection. Stores two lists: one of transactions flagged as possible duplicates
 * and one of transactions cleared as unique.
 */
public class DetectionResult {

    private List<Transaction> possibleDuplicates;
    private List<Transaction> verifiedTransactions;

    public DetectionResult(List<Transaction> possibleDuplicates, List<Transaction> verifiedTransactions) {
        this.possibleDuplicates = possibleDuplicates;
        this.verifiedTransactions = verifiedTransactions;
    }

    public List<Transaction> getPossibleDuplicates() {
        return this.possibleDuplicates;
    }

    public List<Transaction> getVerifiedTransactions() {
        return this.verifiedTransactions;
    }

}
