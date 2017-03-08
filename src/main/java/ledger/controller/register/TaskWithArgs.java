package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ITask} that takes a method with an argument
 */
public class TaskWithArgs<A> extends Task {
    private final List<CallableMethodVoidNoArgs> SuccessEvents;

    private final Thread t;

    public TaskWithArgs(final CallableMethod<A> task, final A args) {
        SuccessEvents = new ArrayList<>();
        t = new Thread(() -> {
            try {
                task.call(args);

                for (CallableMethodVoidNoArgs method :
                        SuccessEvents) {
                    try {
                        method.call();
                    } catch (Exception e) {
                        System.err.println("Post Task Success Event Handler Failed");
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                System.err.println("Task Failed");
                e.printStackTrace();
                for (CallableMethod<Exception> method :
                        getFailureEvents()) {
                    try {
                        method.call(e);
                    } catch (Exception e2) {
                        System.err.println("Task Error Event Handler Failed");
                        e2.printStackTrace();
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
