package ledger.controller.register;

import ledger.controller.CallableMethod;
import ledger.controller.CallableReturnMethod;
import ledger.exception.StorageException;

import java.util.List;

/**
 * Created by gert on 10/11/16.
 */
public class Task<A, R> {

    private List<CallableMethod> SuccessEvents;
    private List<CallableMethod> FailureEvents;
    private Thread t;
    private R result;

    public Task(final CallableMethod<A> task, final A args) {

        t = new Thread(new Runnable() {
            public void run() {
                try {
                        task.call(args);

                    for (CallableMethod method:
                            SuccessEvents){
                        method.call(1);
                    }

                } catch (Exception e){
                    for (CallableMethod method:
                            FailureEvents){
                        try {
                            method.call(e);
                        } catch (Exception e2){
                            //TODO: Log this
                        }
                    }
                }

            }
        });
    }
    public Task(final CallableReturnMethod<A,R> task, final A args)  {

        t = new Thread(new Runnable() {
            public void run() {
                try {
                   result = task.call(args);

                    for (CallableMethod method:
                            SuccessEvents){
                        method.call(1);
                    }

                } catch (Exception e){
                    for (CallableMethod method:
                            FailureEvents){
                        try {
                            method.call(e);
                        } catch (Exception e2){
                            //TODO: Log this
                        }
                    }
                }

            }
        });
    }
    public void RegisterSuccessEvent(CallableMethod func) {
        SuccessEvents.add(func);

    }
    public void RegisterFailureEvent(CallableMethod func){
        FailureEvents.add(func);

    }
    public void startTask(){
        t.start();
    }
    public R waitForResult() {

        try {
            t.join();

        } catch (InterruptedException e) {

        }
        return result;

    }
    public void waitForComplete(){
        try {
            t.join();

        } catch (InterruptedException e) {

        }
    }

}
