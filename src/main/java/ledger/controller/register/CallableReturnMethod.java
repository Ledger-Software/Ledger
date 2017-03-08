package ledger.controller.register;

/**
 * Interface for the runnable portion of {@link ITask} that
 * has a return value and argument of generic type
 */
public interface CallableReturnMethod<A, R> {
    R call(A args) throws Exception;
}
