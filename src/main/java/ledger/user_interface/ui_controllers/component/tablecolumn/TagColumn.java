package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.ui_models.TransactionModel;
import ledger.user_interface.utils.IdenityCellValueCallback;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    public TagColumn() {
        this.setCellValueFactory(new IdenityCellValueCallback<TransactionModel>());
        this.setCellFactory(
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
                                } else {
                                    TagFlowPane flow = new TagFlowPane(model.getTransaction());
                                    setGraphic(flow);
                                }
                            }
                        };
                    }
                });
        this.setSortable(false);
    }

}
