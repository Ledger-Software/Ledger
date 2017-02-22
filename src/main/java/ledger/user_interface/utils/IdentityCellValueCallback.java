package ledger.user_interface.utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Wrapper Class for TableView Cells that returns the object that is used for a row.
 */
public class IdentityCellValueCallback<T> implements Callback<TableColumn.CellDataFeatures<T, T>, ObservableValue<T>> {

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<T,T> param) {
        return new ReadOnlyObjectWrapper(param.getValue());
    }
}
