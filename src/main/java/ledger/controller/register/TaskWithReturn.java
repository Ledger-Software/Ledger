package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ITask} that takes a method with no argument and has a return type
 */
public class TaskWithReturn<R> extends Task{

    private final List<CallableMethod<R>> SuccessEvents;
    private final Thread t;
    private R result;

    public TaskWithReturn(final CallableReturnMethodNoArgs<R> task) {
        SuccessEvents = new ArrayList<>();
        t = new Thread(() -> {
            try {
                result = task.call();

                for (CallableMethod<R> method :
                        SuccessEvents) {
                    try {
                        method.call(result);
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
                        System.err.println("Task Failed");
                        e2.printStackTrace();
                    }
                }
            }

        });
    }

    public void RegisterSuccessEvent(CallableMethod<R> func) {
        SuccessEvents.add(func);
    }

    public R waitForResult() {

        try {
            t.join();

        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    @Override
    Thread getThread() {
        return t;
    }
}
