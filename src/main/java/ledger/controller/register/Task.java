package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJ on 2/5/2017.
 */
public abstract class Task implements ITask {

    private List<CallableMethod<Exception>> failureEvents;

    protected Task() {
        this.failureEvents = new ArrayList<CallableMethod<Exception>>();
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
        } catch (InterruptedException e) {

        }
    }

    List<CallableMethod<Exception>> getFailureEvents() {
        return failureEvents;
    }

    abstract Thread getThread();

}
