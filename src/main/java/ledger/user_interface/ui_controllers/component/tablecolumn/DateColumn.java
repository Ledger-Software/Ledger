package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.DateEventHandler;
import ledger.user_interface.utils.DatePropertyValueFactory;
import ledger.user_interface.utils.LocalDateTableCell;

import java.net.URL;
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
        this.setOnEditCommit(new DateEventHandler());
    }
}
