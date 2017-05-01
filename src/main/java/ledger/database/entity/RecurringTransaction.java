package ledger.database.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Entity that holds a single Recurring Transaction
 */
public class RecurringTransaction extends Transaction {
    private Calendar startDate;
    private Calendar endDate;
    private Calendar nextTriggerDate;
    private Frequency frequency;

    public RecurringTransaction(Calendar startDate, Calendar endDate, Type type, long amount, Account account, Payee payee, List<Tag>
            tagList, Note note, Frequency frequency) {
        this(startDate, endDate, Calendar.getInstance(), type, amount, account, payee, tagList, note, frequency, -1);

        LocalDate localDate = LocalDate.now();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date dateNow = Date.from(instant);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
        if (frequency.equals(Frequency.Daily)) {
            calendar.add(Calendar.DATE, 1);
            this.nextTriggerDate = calendar;
        } else if (frequency.equals(Frequency.Weekly)) {
            calendar.add(Calendar.DATE, 7);
            this.nextTriggerDate = calendar;
        } else if (frequency.equals(Frequency.Monthly)) {
            calendar.add(Calendar.MONTH, 1);
            this.nextTriggerDate = calendar;
        } else if (this.frequency.equals(Frequency.Yearly)) {
            calendar.add(Calendar.YEAR, 1);
        }
    }

    public RecurringTransaction(Calendar startDate, Calendar endDate, Calendar nextTriggerDate, Type type, long amount, Account account, Payee payee, List<Tag>
            tagList, Note note, Frequency frequency, int id) {
        super(new Date(), type, amount, account, payee, false, tagList, note);
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextTriggerDate = nextTriggerDate;
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
     * Gets the Calendar object which represents the date on which this Recurring Transaction will next trigger
     *
     * @return Calendar
     */
    public Calendar getNextTriggerDate() {
        return nextTriggerDate;
    }

    /**
     * Sets the Calendar object which represents the date on which this Recurring Transaction will next trigger
     *
     * @param nextTriggerDate The calendar object representing a date on which this transaction will trigger next
     */
    public void setNextTriggerDate(Calendar nextTriggerDate) {
        this.nextTriggerDate = nextTriggerDate;
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
                ", nextTriggerDate=" + nextTriggerDate.getTime() +
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
