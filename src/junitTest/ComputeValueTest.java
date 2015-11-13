package junitTest;

import core.ComputeValue;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComputeValueTest {
    String expression;

    @Test
    public void testGetValueConstant() throws Exception {
        expression = "3+2-4";
        ComputeValue computeValue = new ComputeValue(expression);
        Assert.assertEquals(1,(int) Integer.valueOf(computeValue.getValue(null)));

        expression = "\"Ali\" + 3 + \" Amoo\"";
        computeValue = new ComputeValue(expression);
        Assert.assertEquals("Ali3 Amoo", computeValue.getValue(null));

        expression = "\"Poone\" + 3 + 2\"";
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