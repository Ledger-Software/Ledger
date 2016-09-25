package ledger.io.events;

import sun.nio.ch.IOStatus;

import java.util.List;

/**
 * Created by CJ on 9/24/2016.
 */
public interface IStatusObserver {
    void IOComplete(List<IOStatus> statusList);
    void IOUpdate(IOStatus status);
}
