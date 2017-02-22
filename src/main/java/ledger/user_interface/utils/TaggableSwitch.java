package ledger.user_interface.utils;

import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;

/**
 * Utility Adapter Class for going from ITaggable back to their Database Controller methods
 */
public class TaggableSwitch {

    public static TaskWithArgs<ITaggable> edit(ITaggable tag) {
        TaskWithArgs task;
        if (tag instanceof Transaction) {
            task = DbController.INSTANCE.editTransaction((Transaction) tag);
        } else if (tag instanceof Payee) {
            task = DbController.INSTANCE.editPayee((Payee) tag);
        } else {
            task = null;
        }
        return task;
    }
}
