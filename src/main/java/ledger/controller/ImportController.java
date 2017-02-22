package ledger.controller;

import ledger.controller.register.TaskWithArgsReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.io.input.*;
import ledger.io.util.Tagger;

import java.io.File;
import java.util.List;

/**
 * Handles all translation from File to putting into storage
 */
public class ImportController {
    public static final ImportController INSTANCE;

    static {
        INSTANCE = new ImportController();
    }

    private ImportController() {
    }

    public Converter[] getAvailableConverters() {
        return Converter.values();
    }

    /**
     * Returns a task that handles the file import with a given Converter type, file path and an account to link too.
     * It will also run duplicate detection.
     *
     * @param type The type of Converter to use
     * @param path The path to the file to convert to TransACT entities
     * @param account The account to add the transactions to
     * @return An object wrapping two lists. One list is the list of successfully converted transactions. One list contains duplicate transactions.
     */
    public TaskWithArgsReturn<Account, ImportFailures> importTransactions(Converter type, File path, Account account) {
        return new TaskWithArgsReturn<>((acc) -> {
            IInAdapter<Transaction> converter = type.method.create(path, acc);

            List<Transaction> trans = converter.convert();
            IgnoredDetector igs = new IgnoredDetector(trans);
            IgnoredDetectionResult ignoredDetectionResult = igs.detectIgnoreTransactions(DbController.INSTANCE.getDb());
            List<Transaction> ignoredTransactions = ignoredDetectionResult.getIgnoredTransactions();
            DuplicateDetector duplicates = new DuplicateDetector(ignoredDetectionResult.getVerifiedTransactions());
            DetectionResult result = duplicates.detectDuplicates(DbController.INSTANCE.getDb());

            List<Transaction> transactions = result.getVerifiedTransactions();
            Tagger tagger = new Tagger(transactions);
            tagger.tagTransactions();

            TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = DbController.INSTANCE.batchInsertTransaction(transactions);
            task.startTask();
            List<Transaction> failedTransactions = task.waitForResult();
            List<Transaction> duplicateTransaction = result.getPossibleDuplicates();
            Tagger taggerDupes = new Tagger(duplicateTransaction);
            taggerDupes.tagTransactions();

            return new ImportFailures(failedTransactions, duplicateTransaction, ignoredTransactions);
        }, account);
    }

    public class ImportFailures {
        public final List<Transaction> failedTransactions;
        public final List<Transaction> duplicateTransactions;
        public final List<Transaction> ignoredTransactions;

        public ImportFailures(List<Transaction> failedTransactions, List<Transaction> duplicateTransactions, List<Transaction> ignoredTransactions) {
            this.failedTransactions = failedTransactions;
            this.duplicateTransactions = duplicateTransactions;
            this.ignoredTransactions = ignoredTransactions;
        }
    }

    /**
     * Enum that holds the available converters in an easy to instantiate format
     */
    public enum Converter {
        AmericanExpressSavingsQFX("American Express Savings QFX", AmericanExpressSavingsQFXConverter::new),
        ChaseBankCSV("Chase Bank CSV", ChaseCSVConverter::new),
        FifthThirdBankQFX("Fifth Third Bank QFX", FifthThirdBankQFXConverter::new),
        FirstDataCorpCSV("First Data Corp CSV", FirstDataCorpCSVConverter::new),
        PayPalCSV("PayPal CSV", PayPalCSVConverter::new),
        USBankCSV("US Bank CSV", USBankCSVConverter::new),
        USBankQFX("US Bank QFX", USBankQFXConverter::new);

        private final String niceName;
        private final ConverterConstructor method;

        Converter(String niceName, ConverterConstructor method) {
            this.niceName = niceName;
            this.method = method;
        }

        @Override
        public String toString() {
            return niceName;
        }

        /**
         * @return A Class with one method that instantiates a IInAdapter
         */
        public ConverterConstructor getMethod() {
            return method;
        }

        /**
         * Abstract Lambda for creating an IInAdapter
         */
        public interface ConverterConstructor {
            IInAdapter<Transaction> create(File file, Account account);
        }

    }
}
