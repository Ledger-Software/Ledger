package ledger.user_interface.utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.database.entity.Transaction;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Tayler How on 1/29/2017.
 */
public class DatePropertyValueFactory implements Callback<TableColumn.CellDataFeatures<Transaction, Date>, ObservableValue<LocalDate>> {
    @Override
    public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<Transaction, Date> param) {
        Date transactionDate = param.getValue().getDate();
        LocalDate localDate = null;
        if (transactionDate != null) {
            // FIXME: This line throws an exception if you edit a dat and immediately try to sort by date, but the DB write persists (no functionality lost)
            LocalDate date = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            localDate = date;
        }
        return new ReadOnlyObjectWrapper(localDate);
    }
}
