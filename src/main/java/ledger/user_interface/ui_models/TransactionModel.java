package ledger.user_interface.ui_models;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by Tayler How on 10/26/2016.
 */
public class TransactionModel {
    // Unused
    private int id;

    // Use DatePropertyValueFactory
    private Date date;

    // Use StringConverters
    private int amount;

    // Use comboboxes and StringConverters
    private Type type;
    private Payee payee;
    private boolean pending;

    // Use Transaction object
    private List<Tag> tags;

    private Transaction transaction;

    public TransactionModel(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();

        if (transaction.getTags() != null) {
            tags = transaction.getTags();
        }

        if (transaction.isPending()) {
            this.pending = true;
        } else {
            this.pending = false;
        }

        this.transaction = transaction;

        this.type = transaction.getType();
        this.payee = transaction.getPayee();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public boolean isPending() {
        return pending;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public List<Tag> getTags() { return this.transaction.getTags(); }
}
