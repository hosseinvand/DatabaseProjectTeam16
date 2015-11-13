package core.conditions;

import core.Condition;

/**
 * Created by hossein on 11/5/15.
 */
public abstract class SimpleCondition extends Condition {

    public SimpleCondition(String conditionExpression) {
        super(conditionExpression);
    }

    public SimpleCondition build(String conditionExpression) {
        conditionExpression.trim();
        if(conditionExpression.contains("TRUE") || conditionExpression.contains("FALSE"))
            return new PrimitiveCondition(conditionExpression);
        else
            return new OperatorCondition(conditionExpression);
    }
}
