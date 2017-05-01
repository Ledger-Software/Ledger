package ledger.user_interface.ui_controllers.component.tablecolumn;

import ledger.database.entity.RecurringTransaction;

public class StartCalendarColumn extends CalenderColumn {

    public StartCalendarColumn() {
        super(RecurringTransaction::getStartDate,RecurringTransaction::setStartDate);
    }
}
