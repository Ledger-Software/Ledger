package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.utils.TaggableSwitch;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shows All Tags for a ITaggable object. Allows the addition there of also
 */
public class TagFlowPane extends FlowPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/FlowPane.fxml";
    private final ITaggable model;

    public TagFlowPane(ITaggable model) {
        this.model = model;
        this.initController(pageLoc, this, "Unable to load Tag Flow");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Tag> tags = model.getTags();
        if(tags != null)
        for(Tag tag: tags) {
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
