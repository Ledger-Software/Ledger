package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation for {@link ITask} that implements {@link #startTask} and {@link #waitForComplete}
 */
public abstract class Task<ReturnType> implements ITask {

    private final List<CallableMethod<Exception>> failureEvents;
    private final List<CallableMethod<ReturnType>> successEvents;

    protected Task() {
        this.failureEvents = new ArrayList<>();
        this.successEvents = new ArrayList<>();
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

    public final ReturnType waitForResult() {
        try {
            getThread().join();

        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return getResult();
    }

    public abstract ReturnType getResult();

    List<CallableMethod<Exception>> getFailureEvents() {
        return failureEvents;
    }

    List<CallableMethod<ReturnType>> getSuccessEvents() {
        return successEvents;
    }

    public void RegisterSuccessEvent(CallableMethod<ReturnType> func) {
        successEvents.add(func);
    }

    public void RegisterSuccessEvent(CallableMethodVoidNoArgs func) {
        successEvents.add((ignored) -> func.call());
    }

    abstract Thread getThread();

}
