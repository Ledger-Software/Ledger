package ledger.controller;

import ledger.controller.register.ITask;

/**
 * Created by CJ on 2/5/2017.
 */
public class UndoAction {

    private ITask task;
    private String message;

    public UndoAction(ITask task, String message) {
        this.task = task;
        this.message = message;
    }

    public void undo() {
        // TODO Raise PopUP or do we want to throw an exception?
//        task.RegisterFailureEvent(failureMethod);
        task.startTask();
        task.waitForComplete();
    }

    public String getMessage() {
        return message;
    }
}
