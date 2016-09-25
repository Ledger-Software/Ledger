package ledger.io.events;

/**
 * Created by CJ on 9/24/2016.
 */
public interface IIOEventGenerator {
    void registerIOComplete(IStatusObserver observer);
    void registerIOUpdate(IStatusObserver observer);
}
