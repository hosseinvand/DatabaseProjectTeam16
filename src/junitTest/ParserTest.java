package junitTest;

import core.ForeignKey;
import core.Parser;
import core.Table;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ParserTest {
    Parser parser = new Parser();

    @Test
    public void createWithPK() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT,NAME VARCHAR,DEG VARCHAR) PRIMARY KEY ID;");
        Table table = Table.getTable("S");
        assertEquals(table.getPk().name, "ID");
    }

    @Test
    public void createWithFKs() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT) PRIMARY KEY ID");
        parser.parse("CREATE TABLE C(ID INT) PRIMARY KEY ID");
        parser.parse("CREATE TABLE SC(STID INT,COID INT,GRADE INT) FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        Table table = Table.getTable("SC");
        ArrayList<ForeignKey> fks = table.getFks();
        assertEquals(fks.size(), 2);
        ForeignKey first = fks.get(0);
        ForeignKey second = fks.get(1);
        assertEquals(first.info.name, "STID");
        assertEquals(first.referToTable.getName(), "S");
        assertEquals(first.onDelete, ForeignKey.Option.CASCADE);
        assertEquals(first.onUpdate, ForeignKey.Option.CASCADE);

        assertEquals(second.info.name, "COID");
        assertEquals(second.referToTable.getName(), "C");
        assertEquals(second.onDelete, ForeignKey.Option.RESTRICT);
        assertEquals(second.onUpdate, ForeignKey.Option.RESTRICT);
    }

    @Test
    public void createWithFKandPK() throws Exception {
        parser.reset();
        parser.parse("CREATE TABLE S(ID INT) PRIMARY KEY ID");
        parser.parse("CREATE TABLE C(ID INT) PRIMARY KEY ID");
        parser.parse("CREATE TABLE SC(STID INT,COID INT,GRADE INT) PRIMARY KEY STID FOREIGN KEY STID REFERENCES S ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY COID REFERENCES C ON DELETE RESTRICT ON UPDATE RESTRICT;");
        Table table = Table.getTable("SC");

        assertEquals(table.getPk().name, "STID");

        ArrayList<ForeignKey> fks = table.getFks();
        assertEquals(fks.size(), 2);
        ForeignKey first = fks.get(0);
        ForeignKey second = fks.get(1);
        assertEquals(first.info.name, "STID");
        assertEquals(first.referToTable.getName(), "S");
        assertEquals(first.onDelete, ForeignKey.Option.CASCADE);
        assertEquals(first.onUpdate, ForeignKey.Option.CASCADE);

        assertEquals(second.info.name, "COID");
        assertEquals(second.referToTable.getName(), "C");
        assertEquals(second.onDelete, ForeignKey.Option.RESTRICT);
        assertEquals(second.onUpdate, ForeignKey.Option.RESTRICT);
    }

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

        String res = parser.parse("SELECT NAME FROM S,C WHERE TRUE;");
        String cor = "NAME\nS111\nS111\nS222\nS222\nS333\nS333\nS444\nS444\n";
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
        parser.parse("INSERT INTO S VALUES (1,\"S222\",\"ms\");");
        parser.parse("INSERT INTO S VALUES (2,\"S333\",\"ss\");");
        parser.parse("INSERT INTO S VALUES (2,\"S444\",\"fs\");");

        String res = parser.parse("SELECT NAME FROM S JOIN C WHERE TRUE;");
        String cor = "NAME\nS111\nS222\nS333\nS444\n";
        assertEquals(cor, res);
    }
}