package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.IdenityCellValueCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    private Callback normalCallback = new Callback<TableColumn<TransactionModel, TransactionModel>, TableCell<TransactionModel, TransactionModel>>() {
        @Override
        public TableCell<TransactionModel, TransactionModel> call(TableColumn<TransactionModel, TransactionModel> param) {
            TableCell<TransactionModel, TransactionModel> cell = new TableCell<TransactionModel, TransactionModel>() {
                @Override
                protected void updateItem(TransactionModel model, boolean empty) {
                    super.updateItem(model, empty);
                    if (model == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(tagsToString(model.getTags()));
                    }
                }
            };
            cell.editingProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    TagFlowPane flow = new TagFlowPane(cell.getItem().getTransaction());
                    cell.setGraphic(flow);
                } else {
                    cell.setGraphic(null);
                    cell.setText(tagsToString(cell.getItem().getTags()));
                }
            });
            return cell;
        }
    };

    public TagColumn() {
        this.setCellValueFactory(new IdenityCellValueCallback<TransactionModel>());
        this.setCellFactory(normalCallback);
        this.setSortable(false);
    }


    private String tagsToString(List<Tag> tags) {
        return String.join(", " , tags.stream().map((tag) -> tag.getName()).collect(Collectors.toList()));
    }
}
