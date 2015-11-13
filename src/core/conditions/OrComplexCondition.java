package core.conditions;

import core.Condition;
import core.Row;
import core.Table;

import java.util.HashSet;

/**
 * Created by hossein on 11/11/15.
 */
public class OrComplexCondition extends ComplexCondition {
    public OrComplexCondition(String conditionExpression) {
        super(conditionExpression);
        parseExpression();
    }

    @Override
    public boolean isValidRow(Row row) {
        return getLeftCondition().isValidRow(row) || getRightCondition().isValidRow(row);
    }

    @Override
    public boolean shouldUseIndex(Table table) {
        return getLeftCondition().shouldUseIndex(table) && getRightCondition().shouldUseIndex(table);
    }

    @Override
    public Row[] getValidRows(Table table) {
        if(!shouldUseIndex(table)) {
            Row[] uncheckedRows;
            Condition checkCondition;
            uncheckedRows = table.getRows().toArray(new Row[table.getRows().size()]);
            checkCondition = this;
            return getValidRowsByIteration(uncheckedRows, checkCondition);
        }
        else {
            Row[] leftRows = getLeftCondition().getValidRows(table);
            Row[] rightRows = getRightCondition().getValidRows(table);
            HashSet<Row> uniqueRows = new HashSet<Row>();
            for (int i = 0; i < leftRows.length; i++) {
                uniqueRows.add(leftRows[i]);
            }
            for (int i = 0; i < rightRows.length; i++) {
                uniqueRows.add(rightRows[i]);
            }
            return uniqueRows.toArray(new Row[uniqueRows.size()]);
        }
    }
}
