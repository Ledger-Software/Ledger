package ledger.exception;

/**
 * Created by CJ on 9/27/2016.
 */
public class LedgerException extends Exception {

    public LedgerException(String message) {
        super(message);
    }

    public LedgerException(String message, Exception e) {
        super(message, e);
    }
}
