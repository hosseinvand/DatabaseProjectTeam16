package core;

import java.util.ArrayList;

public class Parser {
    public void parse(String input) {
        input = input.trim();
        if(input.indexOf("CREATE TABLE") == 0)
            create(input.substring("CREATE TABLE".length()));
        else if(input.indexOf("SELECT") == 0)
            select(input.substring("SELECT".length()));
        else if(input.indexOf("CREATE INDEX") == 0)
            index(input.substring("CREATE INDEX".length()));
        else if(input.indexOf("INSERT INTO") == 0)
            insert(input.substring("INSERT INTO".length()));
        else if(input.indexOf("UPDATE") == 0)
            update(input.substring("UPDATE".length()));
        else if(input.indexOf("DELETE FROM") == 0)
            delete(input.substring("DELETE FROM".length()));
    }

    private void delete(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        String expression = input.substring(0, input.length()-1).trim();
        Table table = Table.getTable(name);
        table.delete(Condition.buildCondition(expression));
    }

    private void update(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        String colname = input.substring(0, input.indexOf("=")).trim();
        String expression = input.substring(input.indexOf("=")+1, input.indexOf("WHERE")).trim();
        String condition = input.substring(input.indexOf("WHERE")+"WHERE".length(), input.length()-1).trim();
        Table table = Table.getTable(name);
        table.update(findColumnInfo(table, colname), expression, Condition.buildCondition(condition));
    }

    private void insert(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        input = input.substring(1, input.length()-2);
        input.trim();
        String[] tokens = input.split(",");
        Object[] values = new Object[tokens.length];
        Table table = Table.getTable(name);
        ColumnInfo[] columnInfos = table.getColumns();
        for (int i = 0; i < tokens.length; i++) {
            if (columnInfos[i].type == ColumnInfo.Type.INT)
                values[i] = Integer.valueOf(tokens[i].trim());
            else
                values[i] = tokens[i].trim();
        }
        table.insert(new Row(columnInfos, values));
    }

    private void index(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        String tableName = getNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        input = input.substring(1, input.length()-2);
        String colName = input.trim();
        Table table = Table.getTable(tableName);
        table.createIndex(name, findColumnInfo(table, colName));
    }

    private ColumnInfo findColumnInfo(Table table, String colName) {
        ColumnInfo[] columnInfos = table.getColumns();
        for (int i = 0; i < columnInfos.length; i++) {
            if(columnInfos[i].name.equals(colName))
                return columnInfos[i];
        }
        return null;
    }

    private void select(String input) {
        input = input.trim();
        String[] tokens = input.split(",");
        String[] colNames = new String[tokens.length];
        for (int i = 0; i < tokens.length-1; i++) {
            colNames[i] = tokens[i].trim();
        }
        colNames[colNames.length-1] = getNextWord(tokens[tokens.length-1]);
        input = deleteNextWord(tokens[tokens.length-1]);
        input = deleteNextWord(input);
        String name = getNextWord(input);
        input = deleteNextWord(input);
        String expression = input.substring(0, input.length()-1).trim();
        Table table = Table.getTable(name);
        ColumnInfo[] columnInfo = table.getColumns();
        ArrayList<ColumnInfo> list = new ArrayList<>();
        for (int i = 0; i < colNames.length; i++) {
            list.add(findColumnInfo(table, colNames[i]));
        }
        table.select((ColumnInfo[]) list.toArray(), Condition.buildCondition(expression));
    }

    private String getNextWord(String input) {
        input = input.trim();
        return input.substring(0, input.indexOf(' ')).trim();
    }

    private String deleteNextWord(String input) {
        input = input.trim();
        return input.substring(input.indexOf(' ')).trim();
    }

    private void create(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        input = input.substring(1, input.length() - 2);
        input = input.trim();
        String[] tokens = input.split(",");
        ColumnInfo[] columnInfos = new ColumnInfo[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
            String[] sub = tokens[i].split(" +");
            columnInfos[i].name = sub[0];
            columnInfos[i].type = sub[1].equals("INT") ? ColumnInfo.Type.INT : ColumnInfo.Type.STRING;
        }
        Table.create(name, columnInfos);
    }

}