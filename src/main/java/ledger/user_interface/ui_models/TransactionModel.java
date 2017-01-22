package ledger.user_interface.ui_models;

import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tayler How on 10/26/2016.
 */
public class TransactionModel {
    private int id;

    // Use TransactionModel
    private String amount;
    private LocalDate date;
    private List<Tag> tags;

    // Use comboboxes and StringConverters
    private Type type;
    private Payee payee;
    private boolean pending;

    private Transaction transaction;

    public TransactionModel(Transaction transaction) {
        this.id = transaction.getId();
        String amountInCents = String.valueOf(transaction.getAmount());
        String dollars = amountInCents.substring(0, amountInCents.length() - 2);
        String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
        this.amount = "$" + dollars + "." + cents;
        if (transaction.getDate() != null) {
            LocalDate date = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            this.date = date;
        } else {
            this.date = null;
        }

        if (transaction.getTagList() != null) {
            tags = transaction.getTagList();
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public List<Tag> getTags() { return this.transaction.getTagList(); }
}
