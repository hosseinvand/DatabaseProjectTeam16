package core.conditions;

import core.Condition;

/**
 * Created by hossein on 11/5/15.
 */
public abstract class ComplexCondition extends Condition {
    private Condition leftCondition;
    private Condition rightCondition;
    private String seperator;
    private int seperatorIndex;

    public Condition getLeftCondition() {
        return leftCondition;
    }

    public Condition getRightCondition() {
        return rightCondition;
    }

    public ComplexCondition(String conditionExpression) {
        super(conditionExpression);
    }

    public ComplexCondition build(String conditionExpression) {
        conditionExpression.trim();
        separateCondition(conditionExpression);
        if(seperator.equals("AND"))
            return new AndComplexCondition(conditionExpression);
        else
            return new OrComplexCondition(conditionExpression);
    }

    protected void separateCondition(String conditionExpression) {
        int paranthesCount = 0;
        for(int i=0; i<conditionExpression.length(); ++i) {
            if(conditionExpression.charAt(i) == '(')
                paranthesCount++;
            if(conditionExpression.charAt(i) == ')')
                paranthesCount--;
            if(paranthesCount != 0)
                continue;
            if(conditionExpression.substring(i, i+3).equals("AND")) {
                seperator = "AND";
                seperatorIndex = i;
                return;
            }
            if(conditionExpression.substring(i, i+2).equals("OR")) {
                seperator = "OR";
                seperatorIndex = i;
                return;
            }
        }
        throw new IllegalStateException("ComplexCondition is not neither AND nor OR");
    }

    @Override
    public void parseExpression() {
        super.parseExpression();
        String conditionExpr = getConditionExpression();
        String leftExpr = conditionExpr.substring(0, seperatorIndex);
        leftExpr.trim();
        leftExpr = leftExpr.substring(1, leftExpr.length()-1); // remove paranthes
        String rightExpr = conditionExpr.substring(seperatorIndex + seperator.length());
        rightExpr.trim();
        rightExpr = rightExpr.substring(1, rightExpr.length()-1);// remove paranthes

        leftCondition = Condition.buildCondition(leftExpr);
        rightCondition = Condition.buildCondition(rightExpr);
    }
}
