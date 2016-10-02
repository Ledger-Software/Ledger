package ledger.database.enity;

import java.util.Date;
import java.util.List;

/**
 * Entity that holds a single Transaction
 */
public class Transaction implements IEntity {
    private Date date;
    private Type type;
    private int amount;
    private Account account;
    private Payee payee;
    private boolean pending;
    private int id;
    private List<Tag> tagList;
    private Note note;

    public Transaction(Date date, Type type, int amount, Account account,
                       Payee payee, boolean pending, List<Tag> tagList, Note note) {
        this(date, type, amount, account, payee, pending, tagList, note, -1);
    }

    public Transaction(Date date, Type type, int amount, Account account,
                       Payee payee, boolean pending, List<Tag> tagList, Note note, int id) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.payee = payee;
        this.pending = pending;
        this.tagList = tagList;
        this.note = note;
        this.id = id;
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
     * @param date
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
     * @param type
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
     * @param amount
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
     * Sets the Transaction account.
     *
     * @param account
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
     * @param payee
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
     * @param pending
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
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the list of Tags associated with the Transaction.
     *
     * @return tagList
     */
    public List<Tag> getTagList() {
        return tagList;
    }

    /**
     * Sets the list of Tags associated with the Transaction.
     *
     * @param tagList
     */
    public void setTagList(List<Tag> tagList) {
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
     * @param note
     */
    public void setNote(Note note) {
        this.note = note;
    }
}
