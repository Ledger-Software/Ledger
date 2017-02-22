package ledger.io.input;

import au.com.bytecode.opencsv.CSVReader;
import ledger.database.entity.Account;
import ledger.database.entity.Transaction;
import ledger.exception.ConverterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Default implementation for CSV Converters
 */
public abstract class AbstractCSVConverter implements IInAdapter<Transaction> {

    protected static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private final File file;
    private final Account account;

    public AbstractCSVConverter(File file, Account account) {
        this.file = file;
        this.account = account;
    }

    /**
     * Converter the data in the CsvConverter's file to TransACT objects.
     *
     * @return A List of Transaction objects
     * @throws ConverterException When unexpected behavior leads to a situation that cannot be recovered from.
     */
    public List<Transaction> convert() throws ConverterException {
        try {
            CSVReader reader = new CSVReader(new FileReader(this.file), ',', '"', 1);
            return readFile(reader);
        } catch (FileNotFoundException e) {
            throw new ConverterException("Specified CSV file could not be found!", e);
        }
    }

    protected abstract List<Transaction> readFile(CSVReader reader) throws ConverterException;

    protected Account getAccount() {
        return this.account;
    }
}
