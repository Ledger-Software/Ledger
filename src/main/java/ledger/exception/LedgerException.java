package ledger.exception;

/**
 * Exceptions that are generated and should be handled by Ledger Software
 */
public abstract class LedgerException extends Exception {

    public LedgerException(String message) {
        super(message);
    }

    public LedgerException(String message, Exception e) {
        super(message, e);
    }
}
