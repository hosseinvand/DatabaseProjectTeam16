package core;

public class Condition {
    private String conditionExpression;
    private boolean notFilter = false;

    public String getConditionExpression() {
        return conditionExpression;
    }

    public boolean getNotFilter() {
        return notFilter;
    }

    public Condition (String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public static Condition buildCondition(String conditionExpression) {
        if(conditionExpression.contains("AND") || conditionExpression.contains("OR"))
            return new ComplexCondition(conditionExpression);
        else
            return new SimpleCondition(conditionExpression);
    }

    public void parseExpression() {
        conditionExpression.trim();
        while (conditionExpression.indexOf("NOT") == 0) {
            notFilter = !notFilter;
            conditionExpression = conditionExpression.substring(3);
        }
    }

    public Row[] getValidRows(Table table) {

        return null;
    }
}
