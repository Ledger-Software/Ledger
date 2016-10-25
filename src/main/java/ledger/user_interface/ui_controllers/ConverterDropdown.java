package ledger.user_interface.ui_controllers;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import ledger.controller.ImportController;
import ledger.database.entity.Transaction;
import ledger.io.input.IInAdapter;

/**
 * Created by CJ on 10/25/2016.
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
