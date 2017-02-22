package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ITask} that takes a method with no argument and has a return type
 */
public class TaskWithReturn<R> extends Task<R> {

    private final Thread t;
    private R result;

    public TaskWithReturn(final CallableReturnMethodNoArgs<R> task) {
        t = new Thread(() -> {
            try {
                result = task.call();

                for (CallableMethod<R> method :
                        getSuccessEvents()) {
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

    @Override
    public R getResult() {
        return result;
    }

    @Override
    Thread getThread() {
        return t;
    }
}
