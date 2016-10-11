package ledger.io.converter;

import ledger.database.enity.Transaction;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Takes a file and converts to Object format for Transactions
 */
public interface IConverter {

    List<Transaction> convert() throws FileNotFoundException;
}
