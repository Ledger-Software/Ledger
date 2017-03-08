package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.database.entity.Transaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.CheckNumberEventHandler;
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
        this.setOnEditCommit(new CheckNumberEventHandler());
    }
}
