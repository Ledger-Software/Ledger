package ledger.database.manager;

import ledger.database.enity.IEntity;

/**
 * Responsible for handling the Input and Output of Business Objects into the Storage Mechanism. Passes responsibility
 * to the IDatabase to handle manipulation of the datastore. The IManager also handles any side effects internal to the
 * application.
 */
public interface IManager<E extends IEntity> {

    /**
     * Inserts the given generic into the database.
     *
     * @param e object to insert
     */
    void insert(E e);

    /**
     * Edits the given generic in the database.
     *
     * @param e object to edit
     */
    void edit(E e);

    /**
     * Deletes the given generic from the database.
     *
     * @param e object to delete
     */
    void delete(E e);

}
