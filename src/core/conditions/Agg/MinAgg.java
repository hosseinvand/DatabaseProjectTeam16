package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public class MinAgg extends Agg{

    public String getValue(ArrayList<Row> rows) {
        int ret=Integer.MAX_VALUE;
        for (Row row : rows) {
            int value = Integer.parseInt(row.getValue(colName));
            ret = Math.min(ret, value);
        }
        return Integer.toString(ret);
    }
}
