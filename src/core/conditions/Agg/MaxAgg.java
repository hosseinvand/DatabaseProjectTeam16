package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public class MaxAgg extends Agg{

    public String getValue(ArrayList<Row> rows) {
        int ret=Integer.MIN_VALUE;
        for (Row row : rows) {
            int value = Integer.parseInt(row.getValue(colName));
            ret = Math.max(ret, value);
        }
        return Integer.toString(ret);
    }
}
