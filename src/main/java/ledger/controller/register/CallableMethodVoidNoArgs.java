package ledger.controller.register;

/**
 * Interface for the runnable portion of {@link ITask} that
 * has no return value or argument
 */
public interface CallableMethodVoidNoArgs {
    void call() throws Exception;
}
