package core.conditions.Agg;

import core.Row;

import java.util.ArrayList;

public class TrueHavingCondition extends HavingCondition {

    @Override
    public boolean isValid(ArrayList<Row> rows) {
        return true;
    }
}
