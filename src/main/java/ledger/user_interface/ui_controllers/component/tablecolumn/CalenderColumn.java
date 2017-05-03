package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.CalendarEventHandler;
import ledger.user_interface.utils.CalendarTableCell;
import ledger.user_interface.utils.CalenderPropertyValueFactory;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;


public abstract class CalenderColumn extends TableColumn<RecurringTransaction, Calendar> implements IUIController, Initializable {

    private static final String pageLoc = "/fxml_files/TableColumn.fxml";
    private final Function<RecurringTransaction, Calendar> getter;
    private final BiConsumer<RecurringTransaction,Calendar> setter;

    public CalenderColumn(Function<RecurringTransaction, Calendar> getter, BiConsumer<RecurringTransaction,Calendar> setter) {
        this.getter = getter;
        this.setter = setter;

        this.initController(pageLoc, this, "Unable to load DateColumn");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new CalenderPropertyValueFactory(getter));
        this.setCellFactory(column -> new CalendarTableCell(this));
        this.setOnEditCommit(new CalendarEventHandler(setter));
    }
}
