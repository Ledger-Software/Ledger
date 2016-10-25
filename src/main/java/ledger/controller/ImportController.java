package ledger.controller;

import ledger.controller.register.TaskWithArgs;
import ledger.database.IDatabase;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.io.importer.Importer;
import ledger.io.input.*;

import java.io.File;
import java.util.List;

/**
 * Created by CJ on 10/23/2016.
 */
public class ImportController {
    public static ImportController INSTANCE;

    public enum Converter  {
        Chase_CSV ("Chase Bank CSV", ChaseConverter::new),
        Qfx ("Quicken QFX", QfxConverter::new);

        private String niceName;
        private ConverterConstructor method;

        Converter(String niceName, ConverterConstructor method) {
            this.niceName = niceName;
            this.method = method;
        }

        public interface ConverterConstructor {
            IInAdapter<Transaction> create(File file, Account account);
        }


        @Override
        public String toString() {
            return niceName;
        }

        public ConverterConstructor getMethod() {
            return method;
        }

    }

    static {
        INSTANCE = new ImportController();
    }

    private ImportController() {}

    public Converter[] getAvaliableConverters() {
        return Converter.values();
    }

    public TaskWithArgs<Account> importTransactions(Converter type, File path, Account account) {
        return new TaskWithArgs<Account>((acc) -> {
            IInAdapter<Transaction> converter = type.method.create(path,acc);

            List<Transaction> trans = converter.convert();

            DuplicateDetector dups = new DuplicateDetector(trans);
            DetectionResult result = dups.detectDuplicates(DbController.INSTANCE.getDb());

            if(result.getPossibleDuplicates().size() != 0) {
                //TODO how to show user
                // Throw Some Exception
                return;
            }

            Importer importer = new Importer();

            IDatabase db = DbController.INSTANCE.getDb();
            List<Transaction> verifiedTrans = result.getVerifiedTransactions();
            if(!importer.importTransactions(db ,verifiedTrans)) {
                // TODO How to show user
                // Throw Some Exception
                return;
            }
        }, account);
    }
}
