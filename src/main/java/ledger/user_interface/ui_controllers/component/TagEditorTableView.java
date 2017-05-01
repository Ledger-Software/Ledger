package ledger.user_interface.ui_controllers.component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import ledger.controller.DbController;
import ledger.controller.register.TaskNoReturn;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.Startup;

import java.net.URL;
import java.util.*;

/**
 * {@link TableView} for showing and editing {@link Payee}
 * Auto pulls from the database
 */
public class TagEditorTableView extends TableView<Tag> implements IUIController, Initializable {
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
        setupColumns();
        setupDelete();
        setupContextMenu();
        updateTableView();
    }

    private void setupColumns() {
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
    }

    private void setupDelete() {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Add ability to delete tags from tableView
        this.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.DELETE) {
                handleDeleteSelectedTagsFromTableView();
            }
        });
    }

    private void setupContextMenu() {
        // Configure right-click context menu
        ContextMenu menu = new ContextMenu();

        // Add right-click option for deleting tags
        MenuItem deleteTagsMenuItem = new MenuItem("Delete Selected Tag(s)");
        menu.getItems().add(deleteTagsMenuItem);
        deleteTagsMenuItem.setOnAction(event -> handleDeleteSelectedTagsFromTableView());

        MenuItem mergeTagsMenuItem = new MenuItem("Merge Selected Tags");
        menu.getItems().add(mergeTagsMenuItem);
        mergeTagsMenuItem.setOnAction(event -> handleMergeTags());

        this.setContextMenu(menu);

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

    // Adapted from TransactionTableView
    private void handleDeleteSelectedTagsFromTableView() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete Tag(s)");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you would like to delete the selected tag(s)?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<Integer> indices = new ArrayList<>();
            // Add indices to new list so they aren't observable
            indices.addAll(this.getSelectionModel().getSelectedIndices());
            if (indices.size() != 0) {

                //TODO: Get around this scary mess
                if (indices.contains(-1)) {
                    indices = this.getSelectionModel().getSelectedIndices();
                }

                for (int i : indices) {
                    Tag tagToDelete = this.getItems().get(i);

                    TaskNoReturn task = DbController.INSTANCE.deleteTag(tagToDelete);
                    task.RegisterFailureEvent((e) -> {
                        asyncUpdateTableView();
                        setupErrorPopup("Error deleting tag(s).", e);
                    });
                    task.startTask();
                    task.waitForComplete();
                }
                updateTableView();
            }
        }
    }

    private void handleMergeTags() {
        List<Integer> indices = new ArrayList<>();
        // Add indices to new list so they aren't observable
        indices.addAll(this.getSelectionModel().getSelectedIndices());
        if (indices.size() < 2) {
            setupErrorPopup("At least two tags must be selected in order to merge.");
            return;
        }

        //TODO: Get around this scary mess
        if (indices.contains(-1)) {
            indices = this.getSelectionModel().getSelectedIndices();
        }

        List<Tag> tagsToMerge = new ArrayList<Tag>();
        for (int i : indices) {
            Tag currentTag = this.getItems().get(i);
            tagsToMerge.add(currentTag);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Merge Tag(s)");
        alert.setHeaderText("WARNING: This operation CANNOT be undone. Are you sure you would like to merge the selected tags?");
        alert.setContentText("All " + tagsToMerge.size() + " tags will be merged into a single tag, with name " + tagsToMerge.get(0).getName() + ".");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TaskNoReturn task = DbController.INSTANCE.mergeTags(tagsToMerge);
            task.RegisterFailureEvent((e) -> {
                asyncUpdateTableView();
                setupErrorPopup("Error merging tag(s).", e);
            });
            task.startTask();
            task.waitForComplete();
            updateTableView();
        }
    }
}
