package ledger.controller;

import ledger.controller.register.ITask;

/**
 * Class used in the implementation of the undo stack in {@link DbController}
 */
public class UndoAction {

    private final ITask task;
    private final String message;

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
