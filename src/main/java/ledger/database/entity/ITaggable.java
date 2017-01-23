package ledger.database.entity;

import java.util.List;

/**
 * Has get and set Tag methods
 */
public interface ITaggable {
    List<Tag> getTags();
    void setTags(List<Tag> tags);
}
