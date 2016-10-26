package ledger.user_interface.ui_models;

import ledger.database.entity.Tag;
import ledger.database.entity.Transaction;

import java.util.List;

/**
 * Created by Tayler How on 10/26/2016.
 */
public class TransactionModel {
    private int id;
    private String amount;
    private String date;
    private String payeeName;
    private String typeName;
    private String tagNames;
    private String pending;
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
        if (transaction.getPayee() != null) {
            this.payeeName = transaction.getPayee().getName();
        } else {
            this.payeeName = "";
        }
        if (transaction.getType() != null) {
            this.typeName = transaction.getType().getName();
        } else {
            this.typeName = "";
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
            this.pending = "Pending";
        } else {
            this.pending = "Cleared";
        }

        this.transaction = transaction;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTagNames() {
        return tagNames;
    }

    public void setTagNames(String tagNames) {
        this.tagNames = tagNames;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
