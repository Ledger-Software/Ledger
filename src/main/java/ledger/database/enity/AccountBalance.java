package ledger.database.enity;

import java.util.Date;

/**
 * Entity that holds a single Account Balance
 */
public class AccountBalance implements IEntity {
    private int accountId;
    private Date date;
    private int amount;

    public AccountBalance(int accountId, Date date, int amount) {
        this.accountId = accountId;
        this.date = date;
        this.amount = amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


}
