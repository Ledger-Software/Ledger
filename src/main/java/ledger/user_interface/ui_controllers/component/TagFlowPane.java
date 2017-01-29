package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shows All Tags for a ITaggable object. Allows the addition there of also
 */
public class TagFlowPane extends HBox implements IUIController {
    private final ITaggable model;

    public TagFlowPane(ITaggable model) {
        this.model = model;
        List<Tag> tags = model.getTags();
        if (tags != null)
            for (Tag tag : tags) {
                RemoveableTag rTag = new RemoveableTag(model, tag);
                this.getChildren().add(rTag);
            }

        Button addButton = new Button();
        addButton.getStyleClass().add("smallButton");
        addButton.setText("Add Tag");
        addButton.setOnAction((ActionEvent event) -> {
            this.getChildren().remove(addButton);

            TagBuilderControl tagBuilder = new TagBuilderControl(model);

            this.getChildren().add(tagBuilder);
        });

        this.getChildren().add(addButton);
    }
}
