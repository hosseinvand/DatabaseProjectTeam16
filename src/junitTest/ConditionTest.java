package junitTest;

import core.ColumnInfo;
import core.Condition;
import core.Row;
import core.Table;
import jdk.internal.org.objectweb.asm.tree.TableSwitchInsnNode;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

public class ConditionTest{
    private Table table;
    private String expression;
    private ColumnInfo[] columnInfos;

    public ConditionTest () throws Table.DBException {
        //create table
        columnInfos = new ColumnInfo[6];
        columnInfos[0] = new ColumnInfo();
        columnInfos[0].name = "ID";
        columnInfos[0].type = ColumnInfo.Type.INT;
        columnInfos[1] = new ColumnInfo();
        columnInfos[1].name = "name";
        columnInfos[1].type = ColumnInfo.Type.STRING;
        columnInfos[2] = new ColumnInfo();
        columnInfos[2].name = "sex";
        columnInfos[2].type = ColumnInfo.Type.STRING;
        columnInfos[3] = new ColumnInfo();
        columnInfos[3].name = "enterance";
        columnInfos[3].type = ColumnInfo.Type.INT;
        columnInfos[4] = new ColumnInfo();
        columnInfos[4].name = "major";
        columnInfos[4].type = ColumnInfo.Type.STRING;
        columnInfos[5] = new ColumnInfo();
        columnInfos[5].name = "passed";
        columnInfos[5].type = ColumnInfo.Type.INT;

        table = Table.create("STD", columnInfos, null, new ArrayList<>());

         //insert Rows
        table.insert(new Row(columnInfos, new String[] {"1", "kambiz", "male", "88", "SW", "12"}));
        table.insert(new Row(columnInfos, new String[] {"2", "shohre", "female", "85", "HW", "42"}));
        table.insert(new Row(columnInfos, new String[] {"3", "zakiye", "female", "91", "SW", "52"}));
        table.insert(new Row(columnInfos, new String[] {"4", "bob", "male", "91", "IT", "32"}));
        table.insert(new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"}));
        table.insert(new Row(columnInfos, new String[] {"6", "ayat", "male", "78", "HW", "12"}));
    }

    @Test
    public void testPrimitveConditionTRUE() throws Exception {
        expression = "FALSE";
        Row[] corRows = new Row[0];
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testPrimitveConditionFALSE() throws Exception {
        expression = "TRUE";
        Row[] corRows = table.getRows().toArray(new Row[table.getRows().size()]);
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testOperatorConditionEQUAL_1() throws Exception {
        expression = "name=\"poone\"";
        Row[] corRows = new Row[1];
        corRows[0] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testOperatorConditionEQUAL_2() throws Exception {
        expression = "passed=62";
        Row[] corRows = new Row[1];
        corRows[0] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testOperatorConditionEQUAL_NOT_EXIST() throws Exception {
        expression = "passed=99";
        Row[] corRows = new Row[0];
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testOperatorConditionLESS() throws Exception {
        expression = "passed<52";
        Row[] corRows = new Row[4];
        corRows[0] = new Row(columnInfos, new String[] {"1", "kambiz", "male", "88", "SW", "12"});
        corRows[1] = new Row(columnInfos, new String[] {"2", "shohre", "female", "85", "HW", "42"});
        corRows[2] = new Row(columnInfos, new String[] {"4", "bob", "male", "91", "IT", "32"});
        corRows[3] = new Row(columnInfos, new String[] {"6", "ayat", "male", "78", "HW", "12"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testOperatorConditionLESS_NOT_EXIST() throws Exception {
        expression = "passed<9";
        Row[] corRows = new Row[0];
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testComplexCondition_OR() throws Exception {
        expression = "(name=\"poone\") OR (name=\"bob\")";
        Row[] corRows = new Row[2];
        corRows[0] = new Row(columnInfos, new String[] {"4", "bob", "male", "91", "IT", "32"});
        corRows[1] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testComplexCondition_OR_2() throws Exception {
        expression = "(sex=\"female\") OR (major=\"IT\")";
        Row[] corRows = new Row[4];
        corRows[0] = new Row(columnInfos, new String[] {"2", "shohre", "female", "85", "HW", "42"});
        corRows[1] = new Row(columnInfos, new String[] {"3", "zakiye", "female", "91", "SW", "52"});
        corRows[2] = new Row(columnInfos, new String[] {"4", "bob", "male", "91", "IT", "32"});
        corRows[3] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testComplexCondition_AND() throws Exception {
        expression = "(sex=\"female\") AND (major=\"IT\")";
        Row[] corRows = new Row[0];
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testComplexCondition_AND_2() throws Exception {
        expression = "(sex=\"male\") AND (major=\"IT\")";
        Row[] corRows = new Row[1];
        corRows[0] = new Row(columnInfos, new String[] {"4", "bob", "male", "91", "IT", "32"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testComplexCondition_AND_3() throws Exception {
        expression = "(NOT sex=\"male\") AND (enterance>90)";
        Row[] corRows = new Row[2];
        corRows[0] = new Row(columnInfos, new String[] {"3", "zakiye", "female", "91", "SW", "52"});
        corRows[1] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }

    @Test
    public void testNOT() throws Exception {
        expression = "NOT sex=\"male\"";
        Row[] corRows = new Row[3];
        corRows[0] = new Row(columnInfos, new String[] {"2", "shohre", "female", "85", "HW", "42"});
        corRows[1] = new Row(columnInfos, new String[] {"3", "zakiye", "female", "91", "SW", "52"});
        corRows[2] = new Row(columnInfos, new String[] {"5", "poone", "female", "92", "SW", "62"});
        Condition condition = Condition.buildCondition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(corRows, rows);
    }
}