package ledger.database.entity;

import ledger.user_interface.utils.IsMatchConverter;

/**
 * Entity that holds a single Ignored Expression
 */
public class IgnoredExpression implements IEntity {
    private int expressionId;
    private String expression;
    private Boolean match;

    /**
     * Full constructor for IgnoredExpression
     * @param id Database assigned ID
     * @param exp Expression used for Match or Contains
     * @param mOR Is Match if True
     */
    public IgnoredExpression(int id, String exp, Boolean mOR){
        this.expressionId = id;
        this.expression = exp;
        this.match = mOR;
    }

    /**
     * Constructor for when there is not database ID yet
     * @param exp Expression used for Match or Contains
     * @param mOR Is Match if True
     */
    public IgnoredExpression( String exp, Boolean mOR){
        this(-1, exp, mOR);
    }

    /**
     * Getter for ExpressionID
     * @return The expression ID
     */
    public int getExpressionId() {
        return expressionId;
    }

    /**
     * Setter for the ExpressionID
     * @param expressionId sets the Database id to this value
     */
    public void setExpressionId(int expressionId) {
        this.expressionId = expressionId;
    }

    /**
     * Getter for the Expression String
     * @return the expression String
     */
    public String getExpression() {
        return expression;
    }
    /**
     * Setter for the Expression
     * @param expression sets the matching String
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Getter for the boolean that represents Match(true) or Contains(false)
     * @return the match/contain boolean
     */
    public Boolean isMatch() {
        return match;
    }

    /**
     * Setter for the boolean that represents Match(true) or Contains(false)
     * @param match if True is Match, Contains otherwise
     */
    public void setMatch(Boolean match) {
        this.match = match;
    }

    /**
     * Generates the String representation of this object
     * @return the string representation of this object
     */
    @Override
    public String toString(){
        return String.format("[ Expression : %s, Rule : %s ]", this.expression, new IsMatchConverter().toString(this.isMatch()));
    }
    /**
     * Determines equality between this IgnoredExpression and another object
     *
     * @param o The object to compare to this IgnoredExpression
     * @return True if this IgnoredExpression is equal to the provided object. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IgnoredExpression)) return false;

        IgnoredExpression igEx = (IgnoredExpression) o;

        return this.getExpression().equals(igEx.getExpression()) && this.isMatch() == igEx.isMatch();

    }
}
