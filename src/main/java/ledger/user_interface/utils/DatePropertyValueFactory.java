package ledger.user_interface.utils;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import ledger.user_interface.ui_models.TransactionModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by Tayler How on 1/29/2017.
 */
public class DatePropertyValueFactory implements Callback<TableColumn.CellDataFeatures<TransactionModel, Date>, ObservableValue<LocalDate>> {
    @Override
    public ObservableValue<LocalDate> call(TableColumn.CellDataFeatures<TransactionModel, Date> param) {
        Date transactionDate = param.getValue().getDate();
        LocalDate localDate = null;
        if (transactionDate != null) {
            LocalDate date = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            localDate = date;
        }
        return new ReadOnlyObjectWrapper(localDate);
    }
}
