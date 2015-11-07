package core;

/**
 * Created by hossein on 11/5/15.
 */
public class SimpleCondition extends Condition{

    public SimpleCondition(String conditionExpression) {
        super(conditionExpression);
    }

    @Override
    public void parseExpression() {
        super.parseExpression();
        String expression = getConditionExpression();
        expression.trim();


    }
}
