package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.utils.DatePropertyValueFactory;
import ledger.user_interface.utils.LocalDateTableCell;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * TableColumn for Dates
 */
public class DateColumn extends TableColumn implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";


    public DateColumn() {
        this.initController(pageLoc, this, "Unable to load DateColumn");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new DatePropertyValueFactory());
        this.setCellFactory(column -> new LocalDateTableCell<>(this));
        this.setOnEditCommit(this.dateEditHandler);
    }

    private EventHandler<CellEditEvent<Transaction, LocalDate>> dateEditHandler = new EventHandler<CellEditEvent<Transaction, LocalDate>>() {
        @Override
        public void handle(CellEditEvent<Transaction, LocalDate> t) {
            Transaction transaction = t.getTableView().getItems().get(t.getTablePosition().getRow());
            LocalDate localDateToSet = t.getNewValue();

            java.util.Date dateToSet = java.sql.Date.valueOf(localDateToSet);

            transaction.setDate(dateToSet);

            TaskWithArgs<Transaction> task = DbController.INSTANCE.editTransaction(transaction);
            task.RegisterFailureEvent((e) -> {
                setupErrorPopup("Error editing transaction date.", e);
            });

            task.startTask();
            task.waitForComplete();
        }
    };
}
