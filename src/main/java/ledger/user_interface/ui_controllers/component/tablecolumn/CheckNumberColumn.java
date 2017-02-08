package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.TransactionTableView;
import ledger.user_interface.utils.AmountStringConverter;
import ledger.user_interface.utils.CheckNumberStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TableColumn for amounts
 */
public class CheckNumberColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";


    public CheckNumberColumn() {
        this.initController(pageLoc, this, "Unable to load CheckNumberColumn");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("checkNumber"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new CheckNumberStringConverter()));
        this.setOnEditCommit(this.checkNumberEditHandler);
    }

    // Transaction table edit event handlers
    private EventHandler<CellEditEvent<Transaction, Integer>> checkNumberEditHandler = new EventHandler<CellEditEvent<Transaction, Integer>>() {
        @Override
        public void handle(CellEditEvent<Transaction, Integer> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Integer checkNumberToSet = t.getNewValue();

            if (checkNumberToSet == null) {
                setupErrorPopup("Provided check number is invalid", new Exception());
                TransactionTableView transactionTableView = (TransactionTableView) getTableView();
                transactionTableView.updateTransactionTableView();
                return;
            }

            transaction.setCheckNumber(checkNumberToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction amount.", e);
            });
            task.startTask();
            task.waitForComplete();
        }
    };
}
