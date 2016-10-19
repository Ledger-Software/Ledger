package ledger.controller.register;

/**
 * Created by gert on 10/11/16.
 */
public interface CallableMethod<A> {
    void call(A args) throws Exception;

}

