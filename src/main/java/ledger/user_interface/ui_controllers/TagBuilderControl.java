package ledger.user_interface.ui_controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.utils.TaggableSwitch;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/23/2017.
 */
public class TagBuilderControl extends GridPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TagBuilder.fxml";
    private final ITaggable model;

    @FXML
    public TextField nameText;
    @FXML
    public TextField descText;
    @FXML
    public Button submitButton;

    public TagBuilderControl(ITaggable model) {
        this.model = model;
        this.initController(pageLoc,this, "Unable to load Tag Builder");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submitButton.setOnAction(this::addTag);
    }

    private void addTag(ActionEvent actionEvent) {
        List<Tag> newTags = model.getTags();
        if (newTags == null){
            newTags = new LinkedList<Tag>();
            model.setTags(newTags);
        }
        newTags = model.getTags();

        newTags.add(new Tag(nameText.getText(), descText.getText()));
        TaskWithArgs task = TaggableSwitch.edit(model);
        task.startTask();
        task.waitForComplete();

        FlowPane parent = ((FlowPane)this.getParent());
        parent.getChildren().remove(this);
    }
}
