package core.conditions;

import com.google.common.collect.Multimap;
import core.ColumnInfo;
import core.ComputeValue;
import core.Row;
import core.Table;

import java.util.Collection;
import java.util.Map;

/**
 * Created by hossein on 11/11/15.
 */
public class OperatorCondition extends SimpleCondition {
    public static final String OPERATOR_EQUAL = "=";
    public static final String OPERATOR_LESS_THAN  = "<";
    public static final String OPERATOR_BIGGER_THAN = ">";
    public static final String OPERATOR_EQUAL_LESS = "<=";
    public static final String OPERATOR_EQUAL_BIGGER = ">=";
    public static final String[] operators = new String[]{OPERATOR_EQUAL, OPERATOR_LESS_THAN, OPERATOR_BIGGER_THAN,
                                                            OPERATOR_EQUAL_LESS, OPERATOR_EQUAL_BIGGER};

    private String operator;
    private String colname;
    private ComputeValue computeValue;

    public OperatorCondition(String conditionExpression) {
        super(conditionExpression);
        parseExpression();
    }

    @Override
    public void parseExpression() {
        super.parseExpression();
        String conditionExp = getConditionExpression();
        for (int i = 0; i < operators.length; i++) {
            if(conditionExp.contains(operators[i])) {
                operator = operators[i];
                colname = conditionExp.substring(0, conditionExp.indexOf(operator));
                colname = colname.trim();
                String coumputeValueExp = conditionExp.substring(conditionExp.indexOf(operator) + operator.length());
                coumputeValueExp = coumputeValueExp.trim();
                computeValue = new ComputeValue(coumputeValueExp);
            }
        }
    }

    @Override
    public boolean isValidRow(Row row) {
        int comparison;
        if(row.getType(colname).equals(ColumnInfo.Type.INT))
            comparison = ((Integer)row.getValue(colname)).compareTo(Integer.valueOf(computeValue.getValue(row)));
        else {
            String left = (String) row.getValue(colname);
            String right = computeValue.getValue(row);
            comparison = left.compareTo(right);
        }

        boolean validness = false;

        if(operator.equals(OPERATOR_EQUAL))
            validness = comparison == 0;
        if(operator.equals(OPERATOR_BIGGER_THAN))
            validness = comparison > 0;
        if(operator.equals(OPERATOR_EQUAL_BIGGER))
            validness = comparison >= 0;
        if(operator.equals(OPERATOR_LESS_THAN))
            validness = comparison < 0;
        if(operator.equals(OPERATOR_EQUAL_LESS))
            validness = comparison <= 0;

        return validness ^ isReverse();
    }

    @Override
    public boolean shouldUseIndex(Table table) {
        if(operator.equals(OPERATOR_EQUAL) && table.isIndexed(colname))
            return true;
        return false;
    }

    @Override
    public Row[] getValidRows(Table table) {
        if(!shouldUseIndex(table) || !computeValue.isConstant())
            return getValidRowsByIteration(table.getRows().toArray(new Row[table.getRows().size()]), this);
        else {
            Multimap<Object, Row> indexmap = table.getIndexMap(colname);
            Collection<Row> rowCollection = indexmap.get(computeValue.getValue(null));
            return rowCollection.toArray(new Row[rowCollection.size()]);
        }
    }
}
