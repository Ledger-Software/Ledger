package ledger.io.events;

/**
 * Generates events based on IO events. These events will be used to updated the UI on progress on reads and writes
 * from the database.
 */
public interface IIOEventGenerator {
    void registerIOComplete(IStatusObserver observer);

    void registerIOUpdate(IStatusObserver observer);
}
