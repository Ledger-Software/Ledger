package ledger.database.entity;

/**
 * Entity that holds a single payee
 */
public class Payee implements IEntity {
    private String name;
    private String description;
    private int id;

    public Payee(String name, String description) {
        this(name, description, -1);
    }

    public Payee(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    /**
     * Returns the Payee name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Payee name.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Payee description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the Payee description.
     *
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Payee ID.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Payee ID.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }
}

