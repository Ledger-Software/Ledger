package ledger.database.entity;

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
     * @param name The new Name
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
     * @param description The new description
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
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Determines equality between this Type and another object. Returns true if this and the other object are equal.
     * Returns false if they are not.
     *
     * @param o The object to compare this Type to.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Type)) return false;

        Type type2 = (Type) o;

        if (!(this.id == type2.getId())) return false;
        if (!this.name.equals(type2.getName())) return false;
        if (!this.description.equals(type2.getDescription())) return false;

        return true;
    }
}