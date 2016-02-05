package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public class SumAgg extends Agg{

    public String getValue(ArrayList<Row> rows) {
        int sum=0;
        for (Row row : rows) {
            int value = Integer.parseInt(row.getValue(colName));
            sum += value;
        }
        return Integer.toString(sum);
    }
}
