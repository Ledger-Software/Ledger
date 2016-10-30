package ledger.io.events;


import ledger.io.events.status.IOStatus;

import java.util.List;

/**
 * This observer will receive events created by the IIOEventGenerator. It will update the UI based on the progress of the
 * backend operations.
 */
public interface IStatusObserver {
    void IOComplete(List<IOStatus> statusList);

    void IOUpdate(IOStatus status);
}
