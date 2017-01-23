package ledger.user_interface.utils;

import ledger.controller.DbController;
import ledger.controller.register.TaskWithArgs;
import ledger.database.entity.ITaggable;
import ledger.database.entity.Payee;
import ledger.database.entity.Transaction;

/**
 * Created by CJ on 1/23/2017.
 */
public class TaggableSwitch {

    public static TaskWithArgs edit(ITaggable tag) {
        TaskWithArgs task;
        if(tag instanceof Transaction) {
            task = DbController.INSTANCE.editTransaction((Transaction) tag);
        } else if (tag instanceof Payee) {
            task = DbController.INSTANCE.editPayee((Payee)tag);
        } else {
            task = null;
        }
        return task;
    }
}
