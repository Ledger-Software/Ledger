package ledger.controller.register;

/**
 * Created by gert on 10/16/16.
 */
public interface CallableReturnMethodNoArgs<R> {
    R call () throws Exception;
}