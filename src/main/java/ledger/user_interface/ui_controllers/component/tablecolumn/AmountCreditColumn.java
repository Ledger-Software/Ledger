package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.AmountEventHandler;
import ledger.user_interface.utils.AmountCreditComparator;
import ledger.user_interface.utils.AmountCreditStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TableColumn for amounts
 */
public class AmountCreditColumn extends AAmountColumn {

    public AmountCreditColumn() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<>("amount"));
        this.setCellFactory(TextFieldTableCell.forTableColumn(new AmountCreditStringConverter()));
        this.setComparator(new AmountCreditComparator());
        this.setOnEditCommit(new AmountEventHandler());
    }
}
