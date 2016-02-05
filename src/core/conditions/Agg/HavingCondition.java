package core.conditions.Agg;

import core.ColumnInfo;
import core.Row;

import java.util.ArrayList;

public class HavingCondition {
    public static final String OPERATOR_EQUAL = "=";
    public static final String OPERATOR_LESS_THAN = "<";
    public static final String OPERATOR_BIGGER_THAN = ">";
    public static final String OPERATOR_EQUAL_LESS = "<=";
    public static final String OPERATOR_EQUAL_BIGGER = ">=";

    public HavingConditionSide left, right;
    public String operator;

    public boolean isValid(ArrayList<Row> rows) {
        int lval = Integer.parseInt(left.getValue(rows));
        int rval = Integer.parseInt(right.getValue(rows));

        int comparison = Integer.compare(lval, rval);

        boolean validness = false;

        if (operator.equals(OPERATOR_EQUAL))
            validness = comparison == 0;
        if (operator.equals(OPERATOR_BIGGER_THAN))
            validness = comparison > 0;
        if (operator.equals(OPERATOR_EQUAL_BIGGER))
            validness = comparison >= 0;
        if (operator.equals(OPERATOR_LESS_THAN))
            validness = comparison < 0;
        if (operator.equals(OPERATOR_EQUAL_LESS))
            validness = comparison <= 0;

        return validness;
    }

    public HavingCondition() {

    }

    public HavingCondition (String expression) {
        if(expression.contains(OPERATOR_EQUAL))
            operator = OPERATOR_EQUAL;
        if(expression.contains(OPERATOR_BIGGER_THAN))
            operator = OPERATOR_BIGGER_THAN;
        if(expression.contains(OPERATOR_EQUAL_BIGGER))
            operator = OPERATOR_EQUAL_BIGGER;
        if(expression.contains(OPERATOR_EQUAL_LESS))
            operator = OPERATOR_EQUAL_LESS;
        if(expression.contains(OPERATOR_LESS_THAN))
            operator = OPERATOR_LESS_THAN;

        left = HavingConditionSide.build(expression.substring(0, expression.indexOf(operator)).trim());
        right = HavingConditionSide.build(expression.substring(expression.indexOf(operator)+1).trim());
    }
}
