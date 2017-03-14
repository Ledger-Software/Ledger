package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;

/**
 * Table Column for handling the amount field of a Transaction Object
 */
public abstract class AAmountColumn extends TableColumn<Transaction, Long> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public AAmountColumn() {
        this.initController(pageLoc, this, "Unable to load AmountColumn/RunningBalanceColumn");
    }
}
