package ledger.io.events.status;

/**
 * Created by CJ on 9/24/2016.
 */
public class TransactionStatus {
    private IOStatusResult result;
    private String message;

    public TransactionStatus(IOStatusResult result, String message)
    {
        this.result = result;
        this.message = message;
    }

    public IOStatusResult getResult() {
        return this.result;
    }

    public String getMessage() {
        return this.message;
    }
}
