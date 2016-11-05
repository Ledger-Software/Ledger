package ledger.controller.register;

/**
 * Created by gert on 10/26/16.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gert on 10/19/16.
 */
public class TaskWithArgsReturn<A, R> {

    private List<CallableMethod<R>> SuccessEvents;
    private List<CallableMethod<Exception>> FailureEvents;
    private Thread t;
    private R result;

    public TaskWithArgsReturn(final CallableReturnMethod<A, R> task, final A arg) {
        SuccessEvents = new ArrayList<CallableMethod<R>>();
        FailureEvents = new ArrayList<CallableMethod<Exception>>();
        t = new Thread(new Runnable() {
            public void run() {
                try {
                    result = task.call(arg);

                    for (CallableMethod<R> method :
                            SuccessEvents) {
                        method.call(result);
                    }

                } catch (Exception e) {
                    for (CallableMethod method :
                            FailureEvents) {
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

    public void RegisterSuccessEvent(CallableMethod<R> func) {
        SuccessEvents.add(func);

    }

    public void RegisterFailureEvent(CallableMethod<Exception> func) {
        FailureEvents.add(func);

    }

    public void startTask() {
        t.start();
    }

    public R waitForResult() {

        try {
            t.join();

        } catch (InterruptedException e) {
            System.out.println(e.getStackTrace());
        }
        return result;

    }

    public void waitForComplete() {
        try {
            t.join();

        } catch (InterruptedException e) {

        }
    }

}
