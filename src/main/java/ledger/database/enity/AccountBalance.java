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

    /**
     * Gets the AccountBalance ID
     * @return id
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Sets the AccountBalance ID
     * @param accountId
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the AccountBalance date
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the AccountBalance date
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the AccountBalance amount
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the AccountBalance amount
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


}
