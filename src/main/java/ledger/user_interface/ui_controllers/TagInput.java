package ledger.user_interface.ui_controllers;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.controller.register.TaskWithArgsReturn;
import ledger.database.entity.IEntity;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.user_interface.utils.TagStringConverter;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by gert on 12/10/16.
 */
public class TagInput extends GridPane implements IUIController, Initializable {
    private static final String pageLog = "/fxml_files/TagInput.fxml";
    @FXML
    private ComboBox<Tag> tagSelector;
    @FXML
    private FlowPane tagContainer;

    private Payee myPayee;

    private ObservableList<Tag> payeeTags;
    private ObservableList<Tag> notPayeeTags;

    public TagInput() {

        this.initController(pageLog, this, "Unable to load User Tag Input");

    }

    /**
     * Sets up action listener for the button on the page
     * <p>
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param fxmlFileLocation The location used to resolve relative paths for the root object, or
     *                         <tt>null</tt> if the location is not known.
     * @param resources        The resources used to localize the root object, or <tt>null</tt> if
     *                         the root object was not localized.
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        this.tagSelector.setDisable(true);
        this.tagContainer.setDisable(true);
        this.payeeTags = FXCollections.observableArrayList();
        this.notPayeeTags = FXCollections.observableArrayList();
        this.payeeTags.addListener(new ListChangeListener<Tag>() {
            @Override
            public void onChanged(Change<? extends Tag> c) {
                while(c.next()) {
                    if (c.wasAdded()) {
                        for (Tag t : c.getAddedSubList()) {
                            tagContainer.getChildren().add(new TagBox(t));
                        }
                    }
                    if (c.wasRemoved()) {
                        for (Tag t : c.getRemoved()) {
                            tagContainer.getChildren().remove(c.getFrom(), c.getFrom() + c.getRemovedSize());
                        }
                    }
                }
            }
        });
        this.tagSelector.setConverter(new TagStringConverter());
        this.tagSelector.setItems(this.notPayeeTags);
        this.tagSelector.setOnAction((event)->{
                    if(!tagSelector.getSelectionModel().isEmpty()) {
                        addPayeeTag(tagSelector.getSelectionModel().getSelectedItem());
                        tagSelector.getSelectionModel().clearSelection();
                    }
                }
        );
        updateLists();
    }

    public void setPayee(Payee payee){
        this.myPayee = payee;
        updateLists();
    }
    private void updateLists(){
        if(this.myPayee!=null){
            TaskWithArgsReturn<Payee,List<Tag>> tagsForPayeeTask =
                    DbController.INSTANCE.getAllTagsForPayee(this.myPayee);
            tagsForPayeeTask.RegisterFailureEvent((e) -> e.printStackTrace());
            tagsForPayeeTask.RegisterSuccessEvent((list) -> {
                this.payeeTags.addAll(list);
                this.tagContainer.setDisable(false);
            });
            TaskWithArgsReturn<Payee,List<Tag>> tagsNotForPayeeTask =
                    DbController.INSTANCE.getAllTagsNotForPayee(this.myPayee);
            tagsNotForPayeeTask.RegisterFailureEvent((e) -> e.printStackTrace());
            tagsNotForPayeeTask.RegisterSuccessEvent((list) -> {
                this.notPayeeTags.addAll(list);
                this.tagSelector.setDisable(false);
            });
            tagsForPayeeTask.startTask();
            tagsNotForPayeeTask.startTask();
        }

    }
    public void removePayeeTag(Tag tag){

        TaskWithArgs<List<IEntity>> deleteTagForPayeeTask = DbController.INSTANCE.deleteTagforPayee(tag,this.myPayee);
        deleteTagForPayeeTask.RegisterSuccessEvent(()->{
            this.payeeTags.remove(tag);
            this.notPayeeTags.add(tag);
        });
        deleteTagForPayeeTask.RegisterFailureEvent((e)-> this.setupErrorPopup("Tag failed to be removed", e));
        deleteTagForPayeeTask.startTask();
    }
    public void addPayeeTag(Tag tag){

        TaskWithArgs<List<IEntity>> addTagForPayeeTask = DbController.INSTANCE.addTagforPayee(tag,this.myPayee);
        addTagForPayeeTask.RegisterSuccessEvent(()->{
            this.payeeTags.add(tag);
            this.notPayeeTags.remove(tag);
        });
        addTagForPayeeTask.RegisterFailureEvent((e)-> this.setupErrorPopup("Tag failed to be added", e));
        addTagForPayeeTask.startTask();
    }

    private class TagBox extends HBox{
        private Tag tag;
        TagBox(Tag tg){
            tag = tg;
            Button removeButton = new Button("X");
            removeButton.setOnAction((evt) -> removePayeeTag(tag));
            Text text = new Text(tag.getName());
            HBox.setMargin(text, new Insets(0, 0, 0, 5));
            getChildren().addAll(text, removeButton);

        }
    }

}
