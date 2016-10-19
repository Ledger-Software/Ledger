package ledger.database.entity;

import java.util.Date;

/**
 * Entity that holds a single Account Balance
 */
public class AccountBalance implements IEntity {
    private Account account;
    private Date date;
    private int amount;

    public AccountBalance(Account account, Date date, int amount) {
        this.account = account;
        this.date = date;
        this.amount = amount;
    }

    /**
     * Gets the AccountBalance ID
     *
     * @return id
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Gets the AccountBalance date
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the AccountBalance date
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the AccountBalance amount
     *
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the AccountBalance amount
     *
     * @param amount the new amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }


}
