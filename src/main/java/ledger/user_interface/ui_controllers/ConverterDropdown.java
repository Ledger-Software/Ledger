package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ledger.controller.ImportController;

/**
 * JavaFx Dropdown that is linked to IInAdapters for Transactions
 */
public class ConverterDropdown extends ChoiceBox<ImportController.Converter> {

    public ConverterDropdown() {
        ImportController.Converter[] items = ImportController.INSTANCE.getAvaliableConverters();
        this.setItems(FXCollections.observableArrayList(items));
    }

    public ImportController.Converter getFileConverter() {
        return this.getValue();
    }

}
