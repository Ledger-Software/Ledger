package ledger.user_interface.ui_controllers.component.tablecolumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ledger.database.entity.Frequency;
import ledger.database.entity.RecurringTransaction;
import ledger.user_interface.ui_controllers.IUIController;
import ledger.user_interface.ui_controllers.component.tablecolumn.event_handler.FrequencyEventHandler;
import ledger.user_interface.utils.FrequencyComparator;
import ledger.user_interface.utils.FrequencyStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FrequencyColumn extends TableColumn<RecurringTransaction, Frequency> implements IUIController, Initializable{
    private static final String pageLoc = "/fxml_files/TableColumn.fxml";

    public FrequencyColumn() {
        this.initController(pageLoc, this, "Unable to load Frequency Column");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setCellValueFactory(new PropertyValueFactory<RecurringTransaction, Frequency>("frequency"));


        List<Frequency> list = new ArrayList<>();
        Arrays.stream(Frequency.values()).forEach((f) -> list.add(f));

        ObservableList<Frequency> observableAllTypes = FXCollections.observableList(list);
        this.setCellFactory(ComboBoxTableCell.forTableColumn(new FrequencyStringConverter(), observableAllTypes));
        this.setOnEditCommit(new FrequencyEventHandler());
        this.setComparator(new FrequencyComparator());
    }
}
