package ledger.exception;

import ledger.controller.ImportController;

/**
 * Exception returned from TransAct's file converters
 */
public class ConverterException extends LedgerException {

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Exception e) {
        super(message, e);
    }
}
