package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gert on 10/19/16.
 */
public class TaskWithArgs<A> extends Task {
    private List<CallableMethodVoidNoArgs> SuccessEvents;

    private Thread t;

    public TaskWithArgs(final CallableMethod<A> task, final A args) {
        SuccessEvents = new ArrayList<CallableMethodVoidNoArgs>();
        t = new Thread(new Runnable() {
            public void run() {
                try {
                    task.call(args);

                    for (CallableMethodVoidNoArgs method :
                            SuccessEvents) {
                        method.call();
                    }

                } catch (Exception e) {
                    for (CallableMethod<Exception> method :
                            getFailureEvents()) {
                        try {
                            method.call(e);
                        } catch (Exception e2) {
                            //TODO: Log this
                        }
                    }
                }

            }
        });
    }

    public void RegisterSuccessEvent(CallableMethodVoidNoArgs func) {
        SuccessEvents.add(func);

    }

    @Override
    Thread getThread() {
        return t;
    }
}
