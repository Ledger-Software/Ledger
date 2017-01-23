package ledger.user_interface.ui_controllers.component;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import ledger.controller.ImportController;
import ledger.user_interface.ui_controllers.IUIController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFx Dropdown that is linked to IInAdapters for Transactions
 */
public class ConverterDropdown extends ChoiceBox<ImportController.Converter> implements IUIController, Initializable {
    private static final String pageLoc = "/fxml_files/ChoiceBox.fxml";

    public ConverterDropdown() {
        this.initController(pageLoc, this, "Unable to load Converter Dropdown");
    }

    public ImportController.Converter getFileConverter() {
        return this.getValue();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImportController.Converter[] items = ImportController.INSTANCE.getAvaliableConverters();
        this.setItems(FXCollections.observableArrayList(items));
    }
}
