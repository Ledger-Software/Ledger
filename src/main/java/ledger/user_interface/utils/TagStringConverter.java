package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Tag;


import java.util.List;

import static ledger.user_interface.utils.InputSanitization.isStringInvalid;

/**
 * Created by gert on 12/10/16.
 */
public class TagStringConverter extends StringConverter<Tag> {

    public Tag fromString(String tagName) {
        if (isStringInvalid(tagName))
            return null;

        // convert from a string to a Type instance
        TaskWithReturn<List<Tag>> getAllTagsTask = DbController.INSTANCE.getAllTags();
        getAllTagsTask.startTask();
        List<Tag> allTags = getAllTagsTask.waitForResult();

        for (Tag currentTag : allTags) {
            if (currentTag.getName().equals(tagName)) {
                return currentTag;
            }
        }

        return new Tag(tagName, tagName);
    }

    public String toString(Tag tag) {
        // convert a Type instance to the text displayed in the choice box
        if (tag != null) {
            return tag.getName();
        } else {
            return "";
        }
    }
}