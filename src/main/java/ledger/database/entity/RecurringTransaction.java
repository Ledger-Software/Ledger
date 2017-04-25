package ledger.database.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Entity that holds a single Recurring Transaction
 */
public class RecurringTransaction extends Transaction {
    private Calendar startDate;
    private Calendar endDate;
    private Frequency frequency;
    private int id;

    public RecurringTransaction(Calendar startDate, Calendar endDate, Type type, long amount, Account account, Payee payee, List<Tag>
            tagList, Note note, Frequency frequency) {
        this(startDate, endDate, type, amount, account, payee, tagList, note, frequency, -1);
    }

    public RecurringTransaction(Calendar startDate, Calendar endDate, Type type, long amount, Account account, Payee payee, List<Tag>
            tagList, Note note, Frequency frequency, int id) {
        super(new Date(), type, amount, account, payee, false, tagList, note);
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.id = id;
    }

    /**
     * Gets the Recurring Transaction's start date
     *
     * @return Calendar
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * Set's the Recurring Transaction's start date
     *
     * @param startDate the new start date
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * Get's the Recurring Transaction's end date
     *
     * @return Calendar
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the Recurring Transaction's end date
     *
     * @param endDate the new end date
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the Recurring Transaction database ID number
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Recurring Transaction database ID number
     *
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the RecurringTransaction frequency
     *
     * @return Frequency
     */
    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency freq) { this.frequency = freq;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecurringTransaction that = (RecurringTransaction) o;

        if (amount != that.amount) return false;
        if (id != that.id) return false;
        if (!startDate.equals(that.startDate)) return false;
        if (!endDate.equals(that.endDate)) return false;
        if (!type.equals(that.type)) return false;
        if (!account.equals(that.account)) return false;
        if (!payee.equals(that.payee)) return false;
        if (tagList != null ? !tagList.equals(that.tagList) : that.tagList != null) return false;
        if (!note.equals(that.note)) return false;
        return frequency == that.frequency;
    }

    @Override
    public int hashCode() {
        int result = startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + account.hashCode();
        result = 31 * result + payee.hashCode();
        result = 31 * result + id;
        result = 31 * result + (tagList != null ? tagList.hashCode() : 0);
        result = 31 * result + note.hashCode();
        result = 31 * result + frequency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RecurringTransaction{" +
                "startDate=" + startDate.getTime() +
                ", endDate=" + endDate.getTime() +
                ", type=" + type +
                ", amount=" + amount +
                ", account=" + account +
                ", payee=" + payee +
                ", id=" + id +
                ", tagList=" + tagList +
                ", note=" + note +
                ", frequency=" + frequency +
                '}';
    }
}