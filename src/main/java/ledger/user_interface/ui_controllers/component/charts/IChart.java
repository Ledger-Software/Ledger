package ledger.user_interface.ui_controllers.component.charts;

import ledger.database.entity.Transaction;

import java.util.List;

/**
 * Interface for Charts that can have their data updated
 */
public interface IChart {
    void updateData(List<Transaction> transactionList);
}
