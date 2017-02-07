package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.NoteEditInputController;
import ledger.user_interface.utils.IdenityCellValueCallback;

/**
 * Created by gert on 2/7/17.
 */
public class NoteColumn extends TableColumn<Transaction, Transaction> implements IUIController {
    public NoteColumn() {


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
                                    NoteEditInputController noteController = new NoteEditInputController(transaction);
                                    setGraphic(noteController);
                                }
                            }
                        };
                    }
                });
        this.setSortable(false);
    }

}
