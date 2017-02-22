package ledger.io.input;

import ledger.database.IDatabase;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;
import ledger.exception.StorageException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Detects duplicates within a transaction file import.
 */
public class  DuplicateDetector {

    private final List<Transaction> transactions;
    private final List<Transaction> possibleDuplicates;
    private final List<Transaction> verifiedTransactions;

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

        databaseTransactions.sort(new TransactionDuplicateComparator());

        for (Transaction importedTrans : transactions) {
            int index = Collections.binarySearch(databaseTransactions, importedTrans, new TransactionDuplicateComparator());
            if (index < 0) {
                verifiedTransactions.add(importedTrans);
            } else {
                possibleDuplicates.add(importedTrans);
            }
        }
        return new DetectionResult(possibleDuplicates, verifiedTransactions);
    }

    private class TransactionDuplicateComparator implements Comparator<Transaction> {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.getAmount() != o2.getAmount()) return (o1.getAmount() < o2.getAmount()) ? -1 : 1;
            if (!o1.getDate().equals(o2.getDate())) return (o1.getDate().compareTo(o2.getDate()));
            if (!o1.getPayee().getName().equals(o2.getPayee().getName()))
                return (o1.getPayee().getName().compareTo(o2.getPayee().getName()));

            if (!o1.getAccount().equals(o2.getAccount())) {
                if (!o1.getAccount().getName().equals(o2.getAccount().getName()))
                    return o1.getAccount().getName().compareTo(o2.getAccount().getName());
                return o1.getAccount().getDescription().compareTo(o2.getAccount().getDescription());
            }

            Type t1 = o1.getType();
            Type t2 = o2.getType();
            if (!t1.getName().equals(t2.getName()))
                return t1.getName().compareTo(t2.getName());
            if (!t1.getDescription().equals(t2.getDescription()))
                return t1.getDescription().compareTo(t2.getDescription());

            return 0;
        }
    }
}
