package core;

import core.conditions.ComplexCondition;
import core.conditions.SimpleCondition;

import java.util.ArrayList;

public abstract class Condition {
    private String conditionExpression;
    private boolean reverse = false;

    public String getConditionExpression() {
        return conditionExpression;
    }

    public boolean isReverse() {
        return reverse;
    }

    public Condition (String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public static Condition buildCondition(String conditionExpression) {
        if(conditionExpression.contains("AND") || conditionExpression.contains("OR"))
            return ComplexCondition.buildCondition(conditionExpression);
        else
            return SimpleCondition.buildCondition(conditionExpression);
    }

    public void parseExpression() {
        conditionExpression.trim();
        while (conditionExpression.indexOf("NOT") == 0) {
            reverse = !reverse;
            conditionExpression = conditionExpression.substring(3);
        }
    }

    public Row[] getValidRowsByIteration(Row[] uncheckedRows, Condition checkCondition) {
        ArrayList<Row> checkedRows = new ArrayList<Row>();
        for (int i = 0; i < uncheckedRows.length; i++) {
            if(checkCondition.isValidRow(uncheckedRows[i]))
                checkedRows.add(uncheckedRows[i]);
        }
        return checkedRows.toArray(new Row[checkedRows.size()]);
    }

    abstract public boolean isValidRow(Row row);

    abstract public boolean shouldUseIndex(Table table);

    abstract public Row[] getValidRows(Table table);
}
