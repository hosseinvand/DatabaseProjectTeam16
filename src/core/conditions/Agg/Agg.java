package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public abstract class Agg extends HavingConditionSide{
    public String colName;

    public static Agg build(String expression) {
        String op = expression.substring(0, expression.indexOf('('));
        Agg agg = null;
        if(op.equals("MAX"))
            agg = new MaxAgg();
        if(op.equals("MIN"))
            agg = new MinAgg();
        if(op.equals("AVG"))
            agg = new AvgAgg();
        if(op.equals("SUM"))
            agg = new SumAgg();
        agg.colName = expression.substring(expression.indexOf('(')+1, expression.indexOf(')'));
        return agg;
    }
}
