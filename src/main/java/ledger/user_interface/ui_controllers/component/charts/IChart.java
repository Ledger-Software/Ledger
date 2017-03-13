package ledger.user_interface.ui_controllers.component.charts;

import ledger.database.entity.Transaction;

import java.util.List;

/**
 * Created by CJ on 3/10/2017.
 */
public interface IChart {
    void updateData(List<Transaction> transactionList);
}
