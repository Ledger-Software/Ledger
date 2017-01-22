package ledger.user_interface.ui_controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import ledger.database.entity.Tag;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/21/2017.
 */
public class TagFlowPane extends FlowPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/FlowPane.fxml";
    private final List<Tag> tags;


    public TagFlowPane(List<Tag> tags) {
        this.tags = tags;
        this.initController(pageLoc, this, "Unable to load Tag Flow");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(Tag tag: tags) {
            RemoveableTag rTag = new RemoveableTag(tag);
            this.getChildren().add(rTag);
        }

        Button addButton = new Button();
        addButton.setText("Add Tag");
        addButton.setOnAction(event -> {
            // Ask user for Name / Description for Tag
        });

        this.getChildren().add(addButton);
    }
}
