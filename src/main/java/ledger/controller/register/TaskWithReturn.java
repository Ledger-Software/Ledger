package ledger.controller.register;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gert on 10/19/16.
 */
public class TaskWithReturn<R> extends Task{

    private List<CallableMethod<R>> SuccessEvents;
    private Thread t;
    private R result;

    public TaskWithReturn(final CallableReturnMethodNoArgs<R> task) {
        SuccessEvents = new ArrayList<CallableMethod<R>>();
        t = new Thread(new Runnable() {
            public void run() {
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
            System.out.println(e.getStackTrace());
        }
        return result;
    }

    @Override
    Thread getThread() {
        return t;
    }
}
