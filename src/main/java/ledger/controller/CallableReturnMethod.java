package ledger.controller;

/**
 * Created by gert on 10/12/16.
 */
public interface CallableReturnMethod<A, R> {
    R call  (A args) throws Exception;
}
