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
                       Payee payee, boolean pending, int id, List<Tag> tagList, Note note) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.payee = payee;
        this.pending = pending;
        this.id = id;
        this.tagList = tagList;
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
