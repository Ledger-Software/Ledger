package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.component.TagFlowPane;
import ledger.user_interface.utils.IdenityCellValueCallback;

/**
 * TableColumn for Tags
 */
public class TagColumn extends TableColumn {

    public TagColumn() {
        this.setCellValueFactory(new IdenityCellValueCallback<Transaction>());
        this.setCellFactory(
                new Callback<TableColumn<Transaction, Transaction>, TableCell<Transaction, Transaction>>() {
                    @Override
                    public TableCell<Transaction, Transaction> call(TableColumn<Transaction, Transaction> param) {
                        return new TableCell<Transaction, Transaction>() {
                            @Override
                            protected void updateItem(Transaction transaction, boolean empty) {
                                super.updateItem(transaction, empty);

                                if (transaction == null || empty) {
                                    setText(null);
                                    setGraphic(null);
                                } else {
                                    TagFlowPane flow = new TagFlowPane(transaction);
                                    setGraphic(flow);
                                }
                            }
                        };
                    }
                });
        this.setSortable(false);
    }

}
