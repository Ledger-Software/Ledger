package ledger.database.entity;

import java.util.List;

/**
 * Entity that holds a single payee
 */
public class Payee implements IEntity, ITaggable{
    private String name;
    private String description;
    private int id;

    private List<Tag> tags;

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

    /**
     * Gets the Payee's list of Tags
     *
     * @return  the list of Tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the Payee's list of Tags
     *
     * @param tags the list of Tags to assign to this Payee
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds a Tag to this Payee's list of Tags
     *
     * @param t the Tag to assign to this Payee
     */
    public void addTag(Tag t) {
        this.tags.add(t);
    }

    @Override
    public String toString() {
        return "Payee{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }

    /**
     * Removes a Tag from this Payee's list of Tags
     *
     * @param t the Tag to remove from this Payee
     */
    public void removeTag(Tag t) {
        this.tags.remove(t);
    }

    /**
     * Determines equality between this Payee and another object
     *
     * @param o The object to compare to this Payee
     * @return True if this Payee is equal to the provided object. False otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Payee)) return false;

        Payee otherPayee = ((Payee) o);

        if (this.getName() == null && otherPayee.getName() != null) return false;
        if (this.getDescription() == null && otherPayee.getDescription() != null) return false;
        if (this.getName() != null && !this.getName().equals((otherPayee.getName()))) return false;
        if (this.getDescription() != null && !this.getDescription().equals((otherPayee.getDescription()))) return false;
        return true;
    }

}

