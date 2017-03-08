package ledger.controller.register;

/**
 * Interface for the runnable portion of {@link ITask} that
 * has no return value but does take an argument
 */
public interface CallableMethod<A> {
    void call(A args) throws Exception;

}

