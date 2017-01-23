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

    protected static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    protected File file;
    protected Account account;

    public AbstractCSVConverter(File file, Account account) {
        this.file = file;
        this.account = account;
    }

    /**
     * Converter the data in the CsvConverter's file to TransACT objects.
     *
     * @return A List of Transaciton objects
     * @throws ConverterException When unexpected behavior leads to a situation that cannot be recovered from.
     */
    public List<Transaction> convert() throws ConverterException {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(this.file), ',', '"', 1);
        } catch (FileNotFoundException e) {
            throw new ConverterException("Specified CSV file could not be found!", e);
        }
        List<Transaction> transactions = readFile(reader);

        return transactions;
    }

    protected abstract List<Transaction> readFile(CSVReader reader) throws ConverterException;
}
