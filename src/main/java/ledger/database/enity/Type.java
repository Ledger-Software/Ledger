package ledger.database.enity;

/**
 * Entity that holds a single Type of account
 */
public class Type implements IEntity {
    private String name;
    private String description;
    private int id;

    public Type(String name, String description) {
        this(name, description, -1);
    }

    public Type(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    /**
     * Gets the Type's name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set's the Type's name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Type's description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Type's Description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Type's ID
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Type's ID
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
}

