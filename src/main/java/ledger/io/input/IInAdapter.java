package ledger.io.input;

import ledger.database.enity.Transaction;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Adapts all external input data to fit the same internal structure that better fits our business logic.
 */
public interface IInAdapter {
    List<Transaction> convert() throws IOException;
}
