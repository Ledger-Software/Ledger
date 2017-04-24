package ledger.database.entity;

/**
 * Possible occurrence frequencies for {@link RecurringTransaction}
 */
public enum Frequency {
    Daily,
    Weekly,
    Monthly,
    Yearly,
    UNKNOWN
}
