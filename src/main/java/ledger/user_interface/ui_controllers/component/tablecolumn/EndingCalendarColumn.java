package ledger.user_interface.ui_controllers.component.tablecolumn;

import ledger.database.entity.RecurringTransaction;

public class EndingCalendarColumn extends CalenderColumn {

    public EndingCalendarColumn() {
        super(RecurringTransaction::getEndDate, RecurringTransaction::setEndDate);
    }
}
