package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation for {@link ITask} that implements {@link #startTask} and {@link #waitForComplete}
 */
public abstract class Task implements ITask {

    private final List<CallableMethod<Exception>> failureEvents;

    protected Task() {
        this.failureEvents = new ArrayList<>();
    }

    @Override
    public final void startTask() {
        getThread().start();
    }

    @Override
    public void RegisterFailureEvent(CallableMethod<Exception> func) {
        this.failureEvents.add(func);
    }

    @Override
    public final void waitForComplete() {
        try {
            getThread().join();
        } catch (InterruptedException ignored) {

        }
    }

    List<CallableMethod<Exception>> getFailureEvents() {
        return failureEvents;
    }

    abstract Thread getThread();

}
