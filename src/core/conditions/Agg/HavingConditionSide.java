package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public abstract class HavingConditionSide {
    public static HavingConditionSide build(String expression) {
        if(expression.indexOf("MAX") == 0 || expression.indexOf("MIN") == 0 ||
                expression.indexOf("SUM") == 0 || expression.indexOf("AVG") == 0)
            return Agg.build(expression);
        else
            return new HavingConditionSideComputeValue(expression);
    }

    public abstract String getValue(ArrayList<Row> rows);
}
