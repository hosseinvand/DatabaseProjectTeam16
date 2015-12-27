package junitTest;

import core.Parser;
import core.Table;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TableConstraintTest {
    Parser parser = new Parser();

    @Test
    public void c1test() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("INSERT INTO S VALUES (111,\"S311\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (211,\"S411\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (311,\"S311\",\"ws\");");
        String res = parser.parse("INSERT INTO S VALUES (111,\"S141\",\"bs\");");
        assertEquals(res, Table.DBException.c1Exception().getMessage());
    }

    @Test
    public void c2test() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(STID INT,COID INT,GRADE INT) FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        String res = parser.parse("INSERT INTO C VALUES (2,\"OOP\",3);");
        assertEquals(res, Table.DBException.c1Exception().getMessage());
        parser.parse("INSERT INTO SC VALUES (111,1,19);");
        res = parser.parse("INSERT INTO SC VALUES (555,1,19);");
        assertEquals(res, Table.DBException.c2Exception().getMessage());
    }


    @Test
    public void deleteChild() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 222,2,19);");
        parser.parse("DELETE FROM SC WHERE GRADE=19;");
        assertEquals(0, Table.getTable("SC").getRows().size());
    }

    @Test
    public void deleteParentNormal() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 222,2,19);");
        parser.parse("DELETE FROM S WHERE ID=333;");
        assertEquals(3, Table.getTable("S").getRows().size());
    }

    @Test
    public void deleteParentRestrict() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 222,2,19);");
        parser.parse("DELETE FROM C WHERE ID=1;");
        assertEquals(2, Table.getTable("C").getRows().size());
    }

    @Test
    public void deleteParentCascade() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        parser.parse("DELETE FROM S WHERE ID=111;");
        assertEquals(3, Table.getTable("S").getRows().size());
        assertEquals(0, Table.getTable("SC").getRows().size());
    }

    @Test
    public void deleteParentCascadeTwoSrc() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("CREATE TABLE FF(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        parser.parse("INSERT INTO FF VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO FF VALUES (2, 111,2,19);");
        parser.parse("DELETE FROM S WHERE ID=111;");
        assertEquals(3, Table.getTable("S").getRows().size());
        assertEquals(0, Table.getTable("SC").getRows().size());
        assertEquals(0, Table.getTable("FF").getRows().size());
    }

    @Test
    public void deleteParentCascadeRestrictPriority() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("CREATE TABLE FF(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE RESTRICT ON UPDATE RESTRICT FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        parser.parse("INSERT INTO FF VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO FF VALUES (2, 111,2,19);");
        parser.parse("DELETE FROM S WHERE ID=111;");
        assertEquals(4, Table.getTable("S").getRows().size());
        assertEquals(2, Table.getTable("SC").getRows().size());
        assertEquals(2, Table.getTable("FF").getRows().size());
    }

    @Test
    public void updateC1Test() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"ms\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        String res = parser.parse("UPDATE S SET ID=555 WHERE DEG=\"bs\";");
        assertEquals(Table.DBException.c1Exception().getMessage(), res);
    }

    @Test
    public void updateC2Test() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("CREATE TABLE FF(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE RESTRICT ON UPDATE RESTRICT FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"fs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        parser.parse("INSERT INTO FF VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO FF VALUES (2, 111,2,19);");
        String beforupdate = Table.getTable("SC").toString();
        String res = parser.parse("UPDATE SC SET STID=555 WHERE GRADE=19;");
        String afterupdate = Table.getTable("SC").toString();
        assertEquals(Table.DBException.c2Exception().getMessage(), res);
        assertEquals(beforupdate, afterupdate);
    }

    @Test
    public void updateRestrict() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("CREATE TABLE FF(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE RESTRICT ON UPDATE RESTRICT FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"fs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        parser.parse("INSERT INTO FF VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO FF VALUES (2, 111,2,19);");
        String beforupdate = Table.getTable("S").toString();
        String res = parser.parse("UPDATE S SET ID=555 WHERE DEG=\"bs\";");
        String afterupdate = Table.getTable("S").toString();
        assertEquals(Table.DBException.RestrictException().getMessage(), res);
        assertEquals(beforupdate, afterupdate);
    }

    @Test
    public void updateCascadeWithC1() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"fs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",3);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,19);");
        String sBeforupdate = Table.getTable("S").toString();
        String res = parser.parse("UPDATE S SET ID=555 WHERE DEG=\"bs\";");
        assertEquals(Table.DBException.c1Exception().getMessage(), res);
        String sAfterupdate = Table.getTable("S").toString();
        assertNotEquals(sBeforupdate, sAfterupdate);
    }

    @Test
    public void updateCascade() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE SC(ID INT, STID INT,COID INT,GRADE INT) PRIMARY KEY ID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"fs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",5);");
        parser.parse("INSERT INTO SC VALUES (1, 111,1,19);");
        parser.parse("INSERT INTO SC VALUES (2, 111,2,18);");
        String sBeforupdate = Table.getTable("S").toString();
        String scbefore = Table.getTable("SC").toString();
        parser.parse("UPDATE S SET ID=85 WHERE DEG=\"bs\";");
        String sAfterupdate = Table.getTable("S").toString();
        String scafter = Table.getTable("SC").toString();
        assertNotEquals(sBeforupdate, sAfterupdate);
        assertNotEquals(scbefore, scafter);
    }

    @Test
    public void updateHalfUpdatePK() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME INT,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("INSERT INTO S VALUES (111,111,\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,666,\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,111,\"ss\");");
        parser.parse("INSERT INTO S VALUES (444,444,\"fs\");");
        String sBeforupdate = Table.getTable("S").toString();
        String res = parser.parse("UPDATE S SET ID=NAME WHERE TRUE;");
        assertEquals(Table.DBException.c1Exception().getMessage(), res);
        String sAfterupdate = Table.getTable("S").toString();
        assertNotEquals(sBeforupdate, sAfterupdate);
        System.out.println(sBeforupdate);
        System.out.println(sAfterupdate);
    }
}