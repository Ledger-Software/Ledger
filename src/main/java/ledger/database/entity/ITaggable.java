package ledger.database.entity;

import java.util.List;

/**
 * Created by CJ on 1/23/2017.
 */
public interface ITaggable {
    List<Tag> getTags();
    void setTags(List<Tag> tags);
}
