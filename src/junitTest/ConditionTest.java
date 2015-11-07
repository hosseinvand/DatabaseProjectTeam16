package junitTest;

import core.ColumnInfo;
import core.Condition;
import core.Row;
import core.Table;
import jdk.internal.org.objectweb.asm.tree.TableSwitchInsnNode;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.TreeMap;

public class ConditionTest{
    private Table table;
    private String expression;
    private Row[] corRows;
    private ColumnInfo[] columnInfos;
    
    @Before
    public void createTable() {
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
        columnInfos[3].type = ColumnInfo.Type.STRING;
        columnInfos[4] = new ColumnInfo();
        columnInfos[4].name = "major";
        columnInfos[4].type = ColumnInfo.Type.STRING;
        columnInfos[5] = new ColumnInfo();
        columnInfos[5].name = "passed";
        columnInfos[5].type = ColumnInfo.Type.INT;

        Table table = Table.create("STD", columnInfos);

        /**  not ready yet
        //insert Rows
        table.insert(new Object[] {1, "kambiz", "male", "88", "SW", "12"});
        table.insert(new Object[] {2, "shohre", "female", "85", "HW", "42"});
        table.insert(new Object[] {3, "zakiye", "female", "91", "SW", "52"});
        table.insert(new Object[] {4, "bob", "male", "91", "IT", "32"});
        table.insert(new Object[] {5, "poone", "female", "92", "SW", "62"});
        table.insert(new Object[] {6, "ayat", "male", "78", "HW", "12"});
         **/
    }

    @Before
    public void createExpressionAndCorRows() {
        expression = "name=\"bob\"";
        corRows = new Row[1];
        corRows[0] = new Row(columnInfos, new Object[] {5, "poone", "female", "92", "SW", "62"});
    }

    @Test
    public void testGetValidRows() throws Exception {
        Condition condition = new Condition(expression);
        Row[] rows = condition.getValidRows(table);
        Assert.assertArrayEquals(rows, corRows);
    }
}