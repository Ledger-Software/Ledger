package ledger.user_interface.utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import ledger.database.entity.RecurringTransaction;

import java.util.Calendar;
import java.util.function.Function;

public class CalenderPropertyValueFactory implements javafx.util.Callback<javafx.scene.control.TableColumn.CellDataFeatures<ledger.database.entity.RecurringTransaction, java.util.Calendar>, javafx.beans.value.ObservableValue<java.util.Calendar>> {

    private final Function<RecurringTransaction, Calendar> getter;

    public CalenderPropertyValueFactory(Function<RecurringTransaction,Calendar> getter) {
        this.getter = getter;
    }

    @Override
    public ObservableValue<Calendar> call(TableColumn.CellDataFeatures<RecurringTransaction, Calendar> param) {
        Calendar calendar = getter.apply(param.getValue());
        return new ReadOnlyObjectWrapper(calendar);
    }
}
