package ledger.user_interface.ui_models;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.property.BooleanProperty;
import ledger.database.entity.Payee;
import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;
import ledger.database.entity.Type;

import java.util.List;

/**
 * Created by Tayler How on 10/26/2016.
 */
public class TransactionModel {
    private int id;
    private String amount;
    private String date;
    private String payeeName;
    private Type type;
    private Payee payee;
    private String tagNames;
    private boolean pending;
    private Transaction transaction;

    public TransactionModel(Transaction transaction) {
        this.id = transaction.getId();
        String amountInCents = String.valueOf(transaction.getAmount());
        String dollars = amountInCents.substring(0, amountInCents.length() - 2);
        String cents = amountInCents.substring(amountInCents.length() - 2, amountInCents.length());
        this.amount = "$" + dollars + "." + cents;
        if (transaction.getDate() != null) {
            this.date = transaction.getDate().toString();
        } else {
            this.date = "";
        }
        this.tagNames = "";
        if (transaction.getTagList() != null) {
            List<Tag> tags = transaction.getTagList();
            for (int i = 0; i < tags.size(); i++) {
                this.tagNames += tags.get(i).getName();
                if (i != tags.size() - 1) {
                    this.tagNames += ", ";
                }
            }
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getTagNames() {
        return tagNames;
    }

    public void setTagNames(String tagNames) {
        this.tagNames = tagNames;
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
}
