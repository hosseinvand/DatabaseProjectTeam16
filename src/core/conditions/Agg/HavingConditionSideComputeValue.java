package core.conditions.Agg;


import core.ComputeValue;
import core.Row;

import java.util.ArrayList;

public class HavingConditionSideComputeValue extends HavingConditionSide{
    public ComputeValue computeValue;

    public HavingConditionSideComputeValue(String expression) {
        computeValue = new ComputeValue(expression);
    }

    @Override
    public String getValue(ArrayList<Row> rows) {
        return computeValue.getValue(rows.get(0));
    }
}
