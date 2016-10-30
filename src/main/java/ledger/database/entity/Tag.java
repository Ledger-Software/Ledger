package ledger.database.entity;

/**
 * Entity that holds a single transaction Tag
 */
public class Tag implements IEntity {
    private String name;
    private String description;
    private int id;

    public Tag(String name, String description) {
        this(name, description, -1);
    }

    public Tag(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    /**
     * Gets the Tag name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Tag name.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Tag description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Tag description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Tag id.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Tag id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }
}
