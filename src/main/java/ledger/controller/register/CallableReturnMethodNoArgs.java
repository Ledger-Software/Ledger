package ledger.controller.register;

/**
 * Interface for the runnable portion of {@link ITask} that
 * has a return value but no arguments
 */
public interface CallableReturnMethodNoArgs<R> {
    R call() throws Exception;
}