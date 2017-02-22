package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ITask} that takes a method with no argument and has a return type
 */
public class TaskNoReturn extends Task<Void> {

    private final Thread t;

    public TaskNoReturn(final CallableMethodVoidNoArgs task) {
        t = new Thread(() -> {
            try {
                task.call();

                for (CallableMethod<Void> method :
                        getSuccessEvents()) {
                    try {
                        method.call(null);
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
    public Void getResult() {
        return null;
    }

    @Override
    Thread getThread() {
        return t;
    }
}
