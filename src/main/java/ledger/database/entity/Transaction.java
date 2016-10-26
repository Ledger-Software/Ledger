package ledger.database.entity;

import java.util.ArrayList;
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
    public List<Tag> getTagList() {
        return tagList;
    }

    /**
     * Sets the list of Tags associated with the Transaction.
     *
     * @param tagList The new List of tags
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
     * @param note The new Note
     */
    public void setNote(Note note) {
        this.note = note;
    }
}
