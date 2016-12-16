package ledger.controller;

import ledger.controller.register.TaskWithArgsReturn;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.io.input.*;

import java.io.File;
import java.util.List;

/**
 * Handles all translation from File to putting into storage
 */
public class ImportController {
    public static ImportController INSTANCE;

    static {
        INSTANCE = new ImportController();
    }

    private ImportController() {
    }

    public Converter[] getAvaliableConverters() {
        return Converter.values();
    }

    /**
     * Returns a task that handles the file import with a given Converter type, file path and an account to link too.
     * It will also run duplicate detection.
     */
    public TaskWithArgsReturn<Account, ImportFailures> importTransactions(Converter type, File path, Account account) {
        return new TaskWithArgsReturn<Account, ImportFailures>((acc) -> {
            IInAdapter<Transaction> converter = type.method.create(path, acc);

            List<Transaction> trans = converter.convert();

            DuplicateDetector dups = new DuplicateDetector(trans);
            DetectionResult result = dups.detectDuplicates(DbController.INSTANCE.getDb());

            TaskWithArgsReturn<List<Transaction>, List<Transaction>> task = DbController.INSTANCE.batchInsertTransaction(result.getVerifiedTransactions());
            task.startTask();
            List<Transaction> failedTransactions = task.waitForResult();
            List<Transaction> duplicateTransaction = result.getPossibleDuplicates();

            return new ImportFailures(failedTransactions, duplicateTransaction);
        }, account);
    }

    public class ImportFailures {
        public List<Transaction> failedTransactions;
        public List<Transaction> duplicateTransactions;

        public ImportFailures(List<Transaction> failedTransactions, List<Transaction> duplicateTransactions) {
            this.failedTransactions = failedTransactions;
            this.duplicateTransactions = duplicateTransactions;
        }
    }

    /**
     * Enum that holds the available converters in an easy to instantiate format
     */
    public enum Converter {
        ChaseBankCSV("Chase Bank CSV", ChaseCSVConverter::new),
        FifthThirdBankQFX("Fifth Third Bank QFX", FifthThirdBankQFXConverter::new),
        USBankQFX("US Bank QFX", USBankQFXConverter::new);

        private String niceName;
        private ConverterConstructor method;

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
