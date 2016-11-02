package ledger.user_interface.utils;

import javafx.util.StringConverter;
import ledger.controller.DbController;
import ledger.controller.register.TaskWithReturn;
import ledger.database.entity.Payee;
import ledger.exception.StorageException;

import java.util.List;

/**
 * Created by Tayler How on 11/1/2016.
 */
public class PayeeStringConverter extends StringConverter<Payee> {

    public Payee fromString(String payeeName) {
        try {
            // convert from a string to a Type instance
            TaskWithReturn<List<Payee>> getAllPayeesTask = DbController.INSTANCE.getAllPayees();
            getAllPayeesTask.startTask();
            List<Payee> allPayees = getAllPayeesTask.waitForResult();

            for (Payee currentPayee : allPayees) {
                if (currentPayee.getName().equals(payeeName)) {
                    return currentPayee;
                }
            }

            return null;
        } catch (StorageException e) {
            // TODO: what do here
            return null;
        }
    }

    public String toString(Payee payee) {
        // convert a Type instance to the text displayed in the choice box
        if (payee != null) {
            return payee.getName();
        } else {
            return "";
        }
    }
}
