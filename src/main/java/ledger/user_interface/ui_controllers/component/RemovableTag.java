package ledger.user_interface.ui_controllers.component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller as part of a TagFlowPane that slows a Tag and allows it to be removed
 */
public class RemovableTag extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TagButton.fxml";
    private final Tag tag;
    private final ITaggable t;

    @FXML
    private Label tagText;
    @FXML
    private Button removeButton;

    public RemovableTag(ITaggable model, Tag tag) {
        this.t = model;
        this.tag = tag;
        this.initController(pageLoc, this, "Unable to load Removable Tag Button");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tagText.setText(tag.getName());
        removeButton.setOnAction(this::removeTag);
    }

    private void removeTag(ActionEvent actionEvent) {
        t.getTags().remove(tag);


        ((TagFlowPane) this.getParent()).editTags(t.getTags());
    }
}
