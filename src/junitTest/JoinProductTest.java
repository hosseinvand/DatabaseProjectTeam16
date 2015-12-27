package junitTest;

import core.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class JoinProductTest {
    Parser parser = new Parser();

    @Test
    public void selectWithProduct() {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");

        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"fs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",5);");

        String res = parser.parse("SELECT NAME,S.DEG,TITLE,C.ID  FROM S,C WHERE TRUE;");
        String cor = "NAME,DEG,TITLE,ID\n" +
                "S111,bs,C++,1\n" +
                "S111,bs,Java Programming,2\n" +
                "S222,ms,C++,1\n" +
                "S222,ms,Java Programming,2\n" +
                "S333,ss,C++,1\n" +
                "S333,ss,Java Programming,2\n" +
                "S444,fs,C++,1\n" +
                "S444,fs,Java Programming,2\n";

        assertEquals(cor, res);
    }

    @Test
    public void selectWithProductConditionAnd() {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");

        parser.parse("INSERT INTO S VALUES (111,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (222,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (333,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (444,\"S444\",\"bs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",5);");

        String res = parser.parse("SELECT NAME,S.DEG,TITLE,C.ID  FROM S,C WHERE (DEG=\"bs\") AND (C.TITLE=\"C++\");");
        String cor = "NAME,DEG,TITLE,ID\n" +
                "S111,bs,C++,1\n" +
                "S444,bs,C++,1\n";

        assertEquals(cor, res);
    }

    @Test
    public void selectWithProductConditionAndBoth() {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR);");
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");

        parser.parse("INSERT INTO S VALUES (2,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (1,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (2,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (2,\"S444\",\"bs\");");
        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",5);");

        String res = parser.parse("SELECT NAME,S.DEG,TITLE,C.ID  FROM S,C WHERE S.ID=C.ID;");
        String cor = "NAME,DEG,TITLE,ID\n" +
                "S111,bs,Java Programming,2\n" +
                "S222,ms,C++,1\n" +
                "S333,ss,Java Programming,2\n" +
                "S444,bs,Java Programming,2\n";

        assertEquals(cor, res);
    }

    @Test
    public void selectWithJoin() {
        parser.reset();
        parser.parse("CREATE TABLE C(ID INT,TITLE VARCHAR,CRD INT) PRIMARY KEY ID;");
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) FOREIGN KEY ID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");

        parser.parse("INSERT INTO C VALUES (1,\"C++\",3);");
        parser.parse("INSERT INTO C VALUES (2,\"Java Programming\",5);");
        parser.parse("INSERT INTO S VALUES (1,\"S111\",\"bs\");");
        parser.parse("INSERT INTO S VALUES (2,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (1,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (2,\"S444\",\"fs\");");

        String res = parser.parse("SELECT NAME,S.DEG,TITLE,C.ID  FROM S JOIN C WHERE TRUE;");
        String cor = "NAME,DEG,TITLE,ID\n" +
                "S111,bs,C++,1\n" +
                "S222,ms,Java Programming,2\n" +
                "S333,ss,C++,1\n" +
                "S444,fs,Java Programming,2\n";

        assertEquals(cor, res);
    }
}