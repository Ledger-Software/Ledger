package ledger.database.entity;

/**
 * An Entity that holds a single Key-Value Pair
 */
public class Setting implements IEntity {
    private final String key;
    private final String value;

    /**
     * @return The Key that this Setting is for
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return The Value that this Setting has
     */
    public String getValue() {
        return this.value;
    }
}
