package junitTest;

import core.ColumnInfo;
import core.ComputeValue;
import core.Row;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComputeValueTest {
    String expression;
    Row row;

    public ComputeValueTest() {
        ColumnInfo[] columnInfos;
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

        row = new Row(columnInfos, new String[]{"5", "poone", "female", "92", "SW", "62"});
    }

    @Test
    public void testGetValueConstant() throws Exception {
        expression = "3+2-4";
        ComputeValue computeValue = new ComputeValue(expression);
        Assert.assertEquals(1,(int) Integer.valueOf(computeValue.getValue(null)));

        expression = "\"Ali\" + 3 + \" Amoo\"";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("Ali3 Amoo", computeValue.getValue(null));

        expression = "\"Poone\" + 3 + 2";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("Poone32", computeValue.getValue(null));

        expression = "3*2+\"Poone\"";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("6Poone", computeValue.getValue(null));

        expression = "\"3+2\"";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("3+2", computeValue.getValue(null));
    }

    @Test
    public void testGetValueVAR() throws Exception {
        expression = "name + \" \" + sex";
        ComputeValue computeValue = new ComputeValue(expression);
        Assert.assertEquals("poone female", computeValue.getValue(row));

        expression = "3 * 2+ 4 + enterance + major";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("102SW", computeValue.getValue(row));

        expression = "3 * 2+ 4 * enterance + major";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("920SW", computeValue.getValue(row));

        expression = "3 *2 + \"angle\" + name";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("6anglepoone", computeValue.getValue(row));

        expression = "3*2 + \"3*2\"";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("63*2", computeValue.getValue(row));
    }

    @Test
    public void testIsConstant() throws Exception {
        expression = "3+2-4*5/4";
        ComputeValue computeValue = new ComputeValue(expression);
        Assert.assertTrue(computeValue.isConstant());

        expression = "3+2-4+ID";
        computeValue = new ComputeValue(expression);
        Assert.assertFalse(computeValue.isConstant());

        expression = "\" \" + \" haha \" + 3";
        computeValue = new ComputeValue(expression);
        Assert.assertTrue(computeValue.isConstant());
    }
}