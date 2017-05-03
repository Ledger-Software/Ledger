package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.IUIController;

import java.util.List;

/**
 * Shows All Tags for a ITaggable object. Allows the addition there of also
 */
public class TagFlowPane extends FlowPane implements IUIController {
    private final ITaggable model;
    private final Control localParent;

    public TagFlowPane(ITaggable model, Control parent) {
        this.model = model;
        this.localParent = parent;
        updateTags();
        this.heightProperty().addListener((observable, oldValue, newValue) -> localParent.setPrefHeight(newValue.doubleValue()));
        this.setPadding(new Insets(0, 0, 35, 0));
    }

    public void updateTags() {
        this.getChildren().clear();
        List<Tag> tags = model.getTags();
        if (tags != null)
            for (Tag tag : tags) {
                RemovableTag rTag = new RemovableTag(model, tag);
                this.getChildren().add(rTag);
            }

        Button addButton = new Button();
        addButton.getStyleClass().add("smallButton");
        addButton.setText("Add Tag");
        addButton.setOnAction((ActionEvent event) -> {
            this.getChildren().remove(addButton);

            TagBuilderControl tagBuilder = new TagBuilderControl(model);

            this.getChildren().add(tagBuilder);
            this.updateBounds();
        });

        this.getChildren().add(addButton);
    }
    public void editTags(List<Tag> tags){
        model.setTags(tags);
        ((TableCell)localParent).commitEdit(model);
    }
}
