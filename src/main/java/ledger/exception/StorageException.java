package ledger.exception;

/**
 * Created by CJ on 9/27/2016.
 */
public class StorageException extends LedgerException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception e) {
        super(message, e);
    }
}
