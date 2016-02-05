package core.conditions;

import core.Condition;

public abstract class SimpleCondition extends Condition {

    public SimpleCondition(String conditionExpression) {
        super(conditionExpression);
    }

    public static SimpleCondition buildCondition(String conditionExpression) {
        conditionExpression = conditionExpression.trim();
        if (conditionExpression.contains("TRUE") || conditionExpression.contains("FALSE"))
            return new PrimitiveCondition(conditionExpression);
        else
            return new OperatorCondition(conditionExpression);
    }
}
