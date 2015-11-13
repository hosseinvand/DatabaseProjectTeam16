package core.conditions;

import core.Condition;
import core.Row;
import core.Table;

import java.util.ArrayList;

public class AndComplexCondition extends ComplexCondition {

    public AndComplexCondition(String conditionExpression, String seperator, int index) {
        super(conditionExpression);
        setSeperator(seperator);
        setSeperatorIndex(index);
        parseExpression();
    }

    @Override
    public boolean isValidRow(Row row) {
        return isReverse() ^ (getLeftCondition().isValidRow(row) && getRightCondition().isValidRow(row));
    }

    @Override
    public boolean shouldUseIndex(Table table) {
        return getLeftCondition().shouldUseIndex(table) || getRightCondition().shouldUseIndex(table);
    }

    @Override
    public Row[] getValidRows(Table table) {
        Row[] uncheckedRows;
        Condition checkCondition;
        if(!shouldUseIndex(table)) {
            uncheckedRows = table.getRows().toArray(new Row[table.getRows().size()]);
            checkCondition = this;
        }
        else {
            uncheckedRows = getLeftCondition().shouldUseIndex(table) ? getLeftCondition().getValidRows(table) :
                    getRightCondition().getValidRows(table);
            checkCondition = getLeftCondition().shouldUseIndex(table) ? getRightCondition() :
                    getLeftCondition();
        }
        return getValidRowsByIteration(uncheckedRows, checkCondition);
    }
}
