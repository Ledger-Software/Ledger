package ledger.database.entity;

import java.util.Date;

/**
 * Entity that holds a single Account Balance
 */
public class AccountBalance implements IEntity {
    private Account account;
    private Date date;
    private long amount;
    private int id;

    public AccountBalance(Account account, Date date, long amount) {
        this.account = account;
        this.date = date;
        this.amount = amount;
    }

    public AccountBalance(Account account, Date date, long amount, int id) {
        this.account = account;

        this.date = date;
        this.amount = amount;
        this.id = id;
    }

    /**
     * Get the AccountBalance's database ID
     *
     * @return database id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the AccountBalance's database ID
     *
     * @param id database id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the Account this Balance is associated with
     *
     * @return id The account associate with this balance
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account associated with this balance
     *
     * @param account the account to assocate the balance with
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the AccountBalance date
     *
     * @return date the existing date
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
     * @return amount the ammount of the current balance
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the AccountBalance amount
     *
     * @param amount the new amount
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountBalance that = (AccountBalance) o;

        if (amount != that.amount) return false;
        if (!account.equals(that.account)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = account != null ? account.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + id;
        return result;
    }
}
