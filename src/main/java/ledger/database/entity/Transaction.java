package ledger.database.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity that holds a single Transaction
 */
public class Transaction implements IEntity, ITaggable {
    private Date date;
    private Type type;
    private int amount;
    private Account account;
    private Payee payee;
    private boolean pending;
    private int id;
    private List<Tag> tagList;
    private Note note;
    private int checkNumber;

    public Transaction(Date date, Type type, int amount, Account account,
                       Payee payee, boolean pending, List<Tag> tagList, Note note, int checkNumber) {
        this(date, type, amount, account, payee, pending, tagList, note, checkNumber, -1);
    }

    public Transaction(Date date, Type type, int amount, Account account,
                       Payee payee, boolean pending, List<Tag> tagList, Note note) {
        this(date, type, amount, account, payee, pending, tagList, note, -1, -1);
    }

    public Transaction(Date date, Type type, int amount, Account account,
                       Payee payee, boolean pending, List<Tag> tagList, Note note, int checkNumber, int id) {
        this.tagList = new ArrayList<Tag>();

        this.date = date;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.payee = payee;
        this.pending = pending;
        if (tagList != null)
            this.tagList.addAll(tagList);
        this.note = note;
        this.id = id;
        this.checkNumber = checkNumber;
    }

    /**
     * Gets the Transaction date.
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the Transaction date.
     *
     * @param date The new date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the Transaction type.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the Transaction type.
     *
     * @param type The new Date
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets the Transaction amount.
     *
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the Transaction amount.
     *
     * @param amount The new amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets the Transaction account.
     *
     * @return account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Gets the check number
     *
     * @return check number
     */
    public int getCheckNumber() {
        return checkNumber;
    }

    /**
     * Sets the check number
     *
     * @param checkNumber check number
     */
    public void setCheckNumber(int checkNumber) {
        this.checkNumber = checkNumber;
    }

    /**
     * Sets the Transaction account.
     *
     * @param account The new account
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the Transaction's payee.
     *
     * @return payee
     */
    public Payee getPayee() {
        return payee;
    }

    /**
     * Sets the Transaction's payee.
     *
     * @param payee The new Payee
     */
    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    /**
     * Determine if Transaction is pending or not.
     *
     * @return pending
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * Sets the Transaction's pending status.
     *
     * @param pending The new pending status
     */
    public void setPending(boolean pending) {
        this.pending = pending;
    }

    /**
     * Gets the Transaction ID.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Transaction ID.
     *
     * @param id The new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the list of Tags associated with the Transaction.
     *
     * @return tagList
     */
    public List<Tag> getTags() {
        return tagList;
    }

    /**
     * Sets the list of Tags associated with the Transaction.
     *
     * @param tagList The new List of tags
     */
    public void setTags(List<Tag> tagList) {
        this.tagList = tagList;
    }

    /**
     * Gets the Transaction's Note
     *
     * @return note
     */
    public Note getNote() {
        return note;
    }

    /**
     * Sets the Transaction's Note
     *
     * @param note The new Note
     */
    public void setNote(Note note) {
        this.note = note;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (amount != that.amount) return false;
        if (pending != that.pending) return false;
        if (id != that.id) return false;
        if (checkNumber != that.checkNumber) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (payee != null ? !payee.equals(that.payee) : that.payee != null) return false;
        if (tagList != null ? !tagList.equals(that.tagList) : that.tagList != null) return false;
        return note != null ? note.equals(that.note) : that.note == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + amount;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (payee != null ? payee.hashCode() : 0);
        result = 31 * result + (pending ? 1 : 0);
        result = 31 * result + id;
        result = 31 * result + (tagList != null ? tagList.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + checkNumber;
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", type=" + type +
                ", amount=" + amount +
                ", account=" + account +
                ", payee=" + payee +
                ", pending=" + pending +
                ", id=" + id +
                ", tagList=" + tagList +
                ", note=" + note +
                ", checkNumber=" + checkNumber +
                '}';
    }
}
