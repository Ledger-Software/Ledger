package ledger.controller.register;

/**
 * Interface for wrapping methods into a Task based framework
 */
public interface ITask {

    void startTask();
    void RegisterFailureEvent(CallableMethod<Exception> func);
    void waitForComplete();

}
