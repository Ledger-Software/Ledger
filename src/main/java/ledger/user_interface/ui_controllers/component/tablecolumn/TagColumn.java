package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.Startup;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.TagEventHandler;
import ledger.user_interface.utils.IdentityCellValueCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    public TagColumn() {
        this.setCellValueFactory(new IdentityCellValueCallback<ITaggable>());
        this.setCellFactory(new ClickAbleTagCell());
        this.setOnEditCommit(new TagEventHandler());
        this.setSortable(false);
    }

    private String tagsToString(List<Tag> tags) {
        return String.join(", ", tags.stream().map(Tag::getName).collect(Collectors.toList()));
    }

    private class ClickAbleTagCell implements Callback<TableColumn<ITaggable, ITaggable>, TableCell<ITaggable, ITaggable>> {
        @Override
        public TableCell<ITaggable, ITaggable> call(TableColumn<ITaggable, ITaggable> param) {
            TableCell<ITaggable, ITaggable> cell = new TableCell<ITaggable, ITaggable>() {
                @Override
                protected void updateItem(ITaggable model, boolean empty) {
                    super.updateItem(model, empty);
                    if (model == null || empty) {
                        setText(null);
                        setGraphic(null);
                        setHeight(30);
                    } else if (isEditing() && getGraphic() != null) {
                        TagFlowPane pane = new TagFlowPane(model, this);
                        setGraphic(pane);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(tagsToString(model.getTags()));
                        setHeight(30);
                    }
                }
                @Override
                public void commitEdit(ITaggable iTaggable){
                    final TableView table = getTableView();
                    if (table != null) {
                        TablePosition position = new TablePosition(getTableView(), getTableRow().getIndex(), getTableColumn());
                        TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(table, position, TableColumn.editCommitEvent(), iTaggable);
                        Event.fireEvent(getTableColumn(), editEvent);
                    }
                    updateItem(iTaggable, false);
                    if (table != null) {
                        table.edit(-1, null);
                    }

                }
            };

            cell.editingProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    TagFlowPane flow = new TagFlowPane(cell.getItem(), cell);
                    cell.setText(null);
                    cell.setGraphic(flow);
                    cell.setPrefHeight(flow.prefHeight(flow.getWidth()) / 2);
                } else {
                    cell.setGraphic(null);
                    cell.setText(tagsToString(cell.getItem().getTags()));
                    cell.setPrefHeight(30);
                }
            });
            return cell;
        }

    }
}
