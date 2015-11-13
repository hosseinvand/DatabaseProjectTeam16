package core.conditions;

import core.Row;
import core.Table;

public class PrimitiveCondition extends SimpleCondition {
    private boolean value;

    public PrimitiveCondition(String conditionExpression) {
        super(conditionExpression);
        parseExpression();
    }

    @Override
    public void parseExpression() {
        super.parseExpression();
        String conditionExp = getConditionExpression();
        conditionExp = conditionExp.trim();
        if (conditionExp.equals("TRUE"))
            value = true;
        else if (conditionExp.equals("FALSE"))
            value = false;
        else
            throw new IllegalStateException("PrimitiveCondition is not neither true nor false");
    }

    @Override
    public boolean isValidRow(Row row) {
        return isReverse() ^ value;
    }

    @Override
    public boolean shouldUseIndex(Table table) {
        return false;
    }

    @Override
    public Row[] getValidRows(Table table) {
        if (!isValidRow(null))
            return new Row[0];
        return table.getRows().toArray(new Row[table.getRows().size()]);
    }
}
