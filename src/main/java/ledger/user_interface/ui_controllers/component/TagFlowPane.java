package ledger.user_interface.ui_controllers.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import ledger.controller.DbController;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
        this.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                localParent.setPrefHeight(newValue.doubleValue());
                System.out.println(newValue.doubleValue());
            }
        });
        this.setMaxHeight(100);
    }

    public void updateTags() {
        this.getChildren().clear();
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
            this.updateBounds();
        });

        this.getChildren().add(addButton);
    }
}
