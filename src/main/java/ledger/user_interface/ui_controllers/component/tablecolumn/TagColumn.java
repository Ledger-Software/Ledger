package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.Tag;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.IdenityCellValueCallback;

import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    private Callback normalCallback = new Callback<TableColumn<TransactionModel, TransactionModel>, TableCell<TransactionModel, TransactionModel>>() {
        @Override
        public TableCell<TransactionModel, TransactionModel> call(TableColumn<TransactionModel, TransactionModel> param) {
            return new TableCell<TransactionModel, TransactionModel>() {
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
        }
    };

    public TagColumn() {
        this.setCellValueFactory(new IdenityCellValueCallback<TransactionModel>());
        this.setCellFactory(normalCallback);
        this.setOnEditStart(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent event) {
                setCellFactory(
                    new Callback<TableColumn<TransactionModel, TransactionModel>, TableCell<TransactionModel, TransactionModel>>() {
                        @Override
                        public TableCell<TransactionModel, TransactionModel> call(TableColumn<TransactionModel, TransactionModel> param) {
                            return new TableCell<TransactionModel, TransactionModel>() {
                                @Override
                                protected void updateItem(TransactionModel model, boolean empty) {
                                    super.updateItem(model, empty);
                                    if (model == null || empty) {
                                        setText(null);
                                        setGraphic(null);
                                    } else if(this.getTableRow().getIndex() == event.getTablePosition().getRow()) {
                                        TagFlowPane flow = new TagFlowPane(model.getTransaction());
                                        setGraphic(flow);
                                    } else {
                                        setText(tagsToString(model.getTags()));
                                    }
                                }
                            };
                        }
                    });
            }
        });
        this.setOnEditCancel(event -> setCellFactory(normalCallback));
        this.setOnEditCommit(event -> setCellFactory(normalCallback));

        this.setSortable(false);
    }


    private String tagsToString(List<Tag> tags) {
        return String.join(", " , tags.stream().map((tag) -> tag.getName()).collect(Collectors.toList()));
    }
}
