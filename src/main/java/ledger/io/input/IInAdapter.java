package ledger.io.input;


import ledger.database.entity.IEntity;
import ledger.exception.ConverterException;

import java.util.List;

/**
 * Adapts all external input data to fit the same internal structure that better fits our business logic.
 */
public interface IInAdapter<T extends IEntity> {

    List<T> convert() throws ConverterException;
}
