package ledger.controller;

import ledger.controller.register.CallableMethod;
import ledger.controller.register.ITask;

/**
 * Created by CJ on 2/5/2017.
 */
public class UndoAction {

    private final CallableMethod<Exception> failureMethod;
    private ITask task;
    private String message;

    public UndoAction(ITask task, String message, CallableMethod<Exception> failureMethod) {
        this.task = task;
        this.message = message;
        this.failureMethod = failureMethod;
    }

    public void undo() {
        if(failureMethod!= null)
            task.RegisterFailureEvent(failureMethod);
        task.startTask();
        task.waitForComplete();
    }
}
