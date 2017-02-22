package ledger.exception;

/**
 * Exception relating to any database actions.
 */
public class StorageException extends LedgerException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Exception e) {
        super(message, e);
    }
}
