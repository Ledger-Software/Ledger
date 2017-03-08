package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.AmountEventHandler;
import ledger.user_interface.utils.AmountStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TableColumn for amounts
 */
public class AmountColumn extends AAmountColumn {

    public AmountColumn() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountStringConverter()));
        this.setOnEditCommit(new AmountEventHandler());
    }
}
