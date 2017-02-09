package ledger.controller.register;

/**
 * Created by gert on 10/26/16.
 */
public interface ITask {

    void startTask();
    void RegisterFailureEvent(CallableMethod<Exception> func);
    void waitForComplete();

}
