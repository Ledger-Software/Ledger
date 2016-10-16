package ledger.io.input;

import ledger.database.IDatabase;
import ledger.database.enity.Payee;
import ledger.database.enity.Transaction;
import ledger.exception.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects duplicates within a transaction file import.
 */
public class DuplicateDetector {

    private List<Transaction> transactions;
    private List<Transaction> possibleDuplicates;
    private List<Transaction> verifiedTransactions;

    public DuplicateDetector(List<Transaction> transactions) {
        this.transactions = transactions;
        this.possibleDuplicates = new ArrayList<>();
        this.verifiedTransactions = new ArrayList<>();
    }

    /**
     * Checks the list of transactions for possible duplicates within the database.
     *
     * @param database The IDatabase to scan for possible duplicates
     * @return A DetectionResult object storing lists of possible duplicates and transactions that have been cleared
     * as unique.
     * @throws StorageException thrown if an exception is thrown while retrieving transactions from the database.
     */
    public DetectionResult detectDuplicates(IDatabase database) throws StorageException {
        List<Transaction> databaseTransactions = database.getAllTransactions();

        boolean verified;
        for (Transaction importedTrans : transactions) {
            verified = true;
            for (Transaction databaseTrans : databaseTransactions) {
                if (importedTrans.getAmount() == databaseTrans.getAmount()) {
                    if (equalByBusinessKeys(importedTrans, databaseTrans)) {
                        possibleDuplicates.add(importedTrans);
                        verified = false;
                    } else {
                        verified = true;
                    }
                } else {
                    verified = true;
                }
            }
            if (verified) verifiedTransactions.add(importedTrans);
        }

        return new DetectionResult(possibleDuplicates, verifiedTransactions);
    }

    private boolean equalByBusinessKeys(Transaction transaction1, Transaction transaction2) {

        if (!transaction1.getDate().equals(transaction2.getDate())) return false;
        if (transaction1.getAmount() != transaction2.getAmount()) return false;
        if (!comparePayees(transaction1.getPayee(), transaction2.getPayee())) return false;

        return true;
    }

    private boolean comparePayees(Payee payee1, Payee payee2) {
        if (!payee1.getName().equals(payee2.getName())) return false;

        return true;
    }
}
