package ledger.database.manager;

import ledger.database.entity.IEntity;

/**
 * A Manager that handles data that has a relation to Time.
 * Has the ability to retrieve data based on Time.
 */
public interface ITimeSeries<T extends IEntity> extends IManager<T> {

}
