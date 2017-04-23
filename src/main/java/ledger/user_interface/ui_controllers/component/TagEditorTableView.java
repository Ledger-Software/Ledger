package ledger.user_interface.ui_controllers.component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * {@link TableView} for showing and editing {@link Payee}
 * Auto pulls from the database
 */
public class TagEditorTableView extends TableView implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TagEditorTableView.fxml";

    @FXML
    private TableColumn<Tag, String> nameColumn;
    @FXML
    private TableColumn<Tag, String> descriptionColumn;

    public TagEditorTableView() {
        this.initController(pageLoc, this, "Error creating Payee Editor");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            String name = event.getNewValue();
            Tag tag = event.getRowValue();
            tag.setName(name);

            TaskNoReturn task = DbController.INSTANCE.editTag(tag);
            task.startTask();
            task.waitForComplete();
        });

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            String description = event.getNewValue();
            Tag tag = event.getRowValue();
            tag.setDescription(description);

            TaskNoReturn task = DbController.INSTANCE.editTag(tag);
            task.startTask();
            task.waitForComplete();
        });


        DbController.INSTANCE.registerPayeeSuccessEvent((ignored) -> this.asyncUpdateTableView());
        updateTableView();
    }

    private void updateTableView() {
        TaskWithReturn<List<Tag>> task = DbController.INSTANCE.getAllTags();
        task.startTask();
        List<Tag> tags = task.waitForResult();

        // Hide our placeholder empty string Tag from user
        Tag currentTag = null;
        for(int i = 0; i < tags.size(); i++) {
            currentTag = tags.get(i);
            if (currentTag.getName().equals("") && currentTag.getDescription().equals("")) {
                tags.remove(currentTag);
            }
        }

        this.getItems().clear();
        this.getItems().addAll(tags);
    }

    private void asyncUpdateTableView() {
        Startup.INSTANCE.runLater(this::updateTableView);
    }
}
