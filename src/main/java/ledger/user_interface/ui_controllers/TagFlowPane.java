package ledger.user_interface.ui_controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_models.TransactionModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by CJ on 1/21/2017.
 */
public class TagFlowPane extends FlowPane implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/FlowPane.fxml";
    private final TransactionModel model;


    public TagFlowPane(TransactionModel model) {
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
        addButton.setText("Add Tag");
        addButton.setOnAction(event -> {
           Transaction t = model.getTransaction();
            t.getTagList().add(new Tag("NewTag", "Desc"));
            TaskWithArgs task = DbController.INSTANCE.editTransaction(t);
            task.startTask();
            task.waitForComplete();
        });

        this.getChildren().add(addButton);
    }
}
