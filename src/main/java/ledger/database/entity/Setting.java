package ledger.database.entity;

/**
 * An Entity that holds a single Key-Value Pair
 */
public class Setting implements IEntity {
    private String key;
    private String value;

    /**
     * @return The Key that this Setting is for
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return The Value that htis Setting is for
     */
    public String getValue() {
        return this.value;
    }
}
