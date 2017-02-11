package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.utils.IdenityCellValueCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    private Callback normalCallback = new Callback<TableColumn<ITaggable, ITaggable>, TableCell<ITaggable, ITaggable>>() {
        @Override
        public TableCell<ITaggable, ITaggable> call(TableColumn<ITaggable, ITaggable> param) {
            TableCell<ITaggable, ITaggable> cell = new TableCell<ITaggable, ITaggable>() {
                @Override
                protected void updateItem(ITaggable model, boolean empty) {
                    super.updateItem(model, empty);
                    if (model == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (getGraphic() == null) {
                            setText(tagsToString(model.getTags()));
                        }
                    }
                }
            };
            cell.editingProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    TagFlowPane flow = new TagFlowPane(cell.getItem(), cell);
                    cell.setText(null);
                    cell.setGraphic(flow);
                    cell.setPrefHeight(flow.heightProperty().doubleValue());
                } else {
                    cell.setGraphic(null);
                    cell.setText(tagsToString(cell.getItem().getTags()));
                    cell.requestLayout();
                }
            });
            return cell;
        }
    };

    public TagColumn() {
        this.setCellValueFactory(new IdenityCellValueCallback<ITaggable>());
        this.setCellFactory(normalCallback);
        this.setSortable(false);
    }


    private String tagsToString(List<Tag> tags) {
        return String.join(", " , tags.stream().map((tag) -> tag.getName()).collect(Collectors.toList()));
    }
}
