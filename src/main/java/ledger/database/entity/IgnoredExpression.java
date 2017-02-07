package ledger.database.entity;

/**
 * Created by gert on 2/7/17.
 */
public class IgnoredExpression implements IEntity {
    private int expressionId;
    private String expression;
    private Boolean matchOrContain;

    public IgnoredExpression(int id, String exp, Boolean mOR){
        this.expressionId = id;
        this.expression = exp;
        this.matchOrContain = mOR;
    }

    public IgnoredExpression( String exp, Boolean mOR){
        this(-1, exp, mOR);
    }

    public int getExpressionId() {
        return expressionId;
    }

    public void setExpressionId(int expressionId) {
        this.expressionId = expressionId;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Boolean getMatchOrContain() {
        return matchOrContain;
    }

    public void setMatchOrContain(Boolean matchOrContain) {
        this.matchOrContain = matchOrContain;
    }
}
