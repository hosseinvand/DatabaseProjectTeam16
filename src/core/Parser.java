package core;

import java.util.ArrayList;

import static java.lang.Math.min;

public class Parser {
    public String parse(String input) {
        input = input.trim();
        if (input.indexOf("CREATE TABLE") == 0)
            return create(input.substring("CREATE TABLE".length()));
        else if (input.indexOf("SELECT") == 0)
            return select(input.substring("SELECT".length()));
        else if (input.indexOf("CREATE INDEX") == 0)
            return index(input.substring("CREATE INDEX".length()));
        else if (input.indexOf("INSERT INTO") == 0)
            return insert(input.substring("INSERT INTO".length()));
        else if (input.indexOf("UPDATE") == 0)
            return update(input.substring("UPDATE".length()));
        else if (input.indexOf("DELETE FROM") == 0)
            return delete(input.substring("DELETE FROM".length()));
        return null;
    }

    private String delete(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        String expression = input.substring(0, input.length() - 1).trim();
        Table table = Table.getTable(name);
        try {
            table.delete(Condition.buildCondition(expression));
        } catch (Table.DBException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    private String update(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        String colname = input.substring(0, input.indexOf("=")).trim();
        String expression = input.substring(input.indexOf("=") + 1, input.indexOf("WHERE")).trim();
        String condition = input.substring(input.indexOf("WHERE") + "WHERE".length(), input.length() - 1).trim();
        Table table = Table.getTable(name);
        try {
            table.update(findColumnInfo(table, colname), expression, Condition.buildCondition(condition));
        } catch (Table.DBException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return null;
    }

    private String insert(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(deleteNextWord(input));
        input = input.substring(1, input.length() - 2);
        input.trim();
        String[] tokens = input.split(",");
        String[] values = new String[tokens.length];
        Table table = Table.getTable(name);
        ColumnInfo[] columnInfos = table.getColumns();
        for (int i = 0; i < tokens.length; i++) {
            if (columnInfos[i].type == ColumnInfo.Type.INT)
                values[i] = tokens[i].trim();
            else
                values[i] = tokens[i].trim().replace("\"", "");
        }
        try {
            table.insert(new Row(columnInfos, values));
        } catch (Table.DBException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        System.out.println("RECORD INSERTED");
        return "RECORD INSERTED";
    }

    private String index(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        String tableName = getNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        input = input.substring(1, input.length() - 2);
        String colName = input.trim();
        Table table = Table.getTable(tableName);
        table.createIndex(name, findColumnInfo(table, colName));
        System.out.println("INDEX CREATED");
        return "INDEX CREATED";
    }

    private ColumnInfo findColumnInfo(Table table, String colName) {
        ColumnInfo[] columnInfos = table.getColumns();
        for (int i = 0; i < columnInfos.length; i++) {
            if (columnInfos[i].name.equals(colName))
                return columnInfos[i];
        }
        return null;
    }

    private ColumnInfo findColumnInfo(ColumnInfo[] columnInfos, String colName) {
        for (int i = 0; i < columnInfos.length; i++) {
            if (columnInfos[i].name.equals(colName))
                return columnInfos[i];
        }
        return null;
    }

    private String select(String input) {
        input = input.trim();
        String[] tokens = input.substring(0, input.indexOf("FROM")).trim().split(",");
        String[] colNames = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            colNames[i] = tokens[i].trim();
        }
        input = input.substring(input.indexOf("FROM"));
        input = deleteNextWord(input);
        String from = input.substring(0, input.indexOf("WHERE"));
        Table table;
        if(from.contains(",")) {
            table = Table.product(Table.getTable(from.split(",")[0].trim()), Table.getTable(from.split(",")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else if(from.contains("JOIN")) {
            table = Table.join(Table.getTable(from.split("JOIN")[0].trim()), Table.getTable(from.split("JOIN")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else {
            table = Table.getTable(from.trim());
        }
        input = input.substring(input.indexOf("WHERE"));
        input = deleteNextWord(input);
        String expression = input.substring(0, input.length() - 1).trim();
        ArrayList<ColumnInfo> list = new ArrayList<>();
        for (int i = 0; i < colNames.length; i++) {
            list.add(findColumnInfo(table, colNames[i]));
        }
        Table selected = table.select(list.toArray(new ColumnInfo[list.size()]), Condition.buildCondition(expression));
        if (selected.getRows().size() == 0) {
            System.out.println("NO RESULTS");
            return "NO RESULTS";
        }
        else {
            System.out.print(selected.toString());
            return selected.toString();
        }
    }

    private String getNextWord(String input) {
        input = input.trim();
        int spaceIndex = input.indexOf(' ');
        if (spaceIndex == -1)
            spaceIndex = Integer.MAX_VALUE;
        int parantIndex = input.indexOf('(');
        if (parantIndex == -1)
            parantIndex = Integer.MAX_VALUE;
        return input.substring(0, min(min(spaceIndex, parantIndex), input.length())).trim();
    }

    private String deleteNextWord(String input) {
        input = input.trim();
        int spaceIndex = input.indexOf(' ');
        if (spaceIndex == -1)
            spaceIndex = Integer.MAX_VALUE;
        int parantIndex = input.indexOf('(');
        if (parantIndex == -1)
            parantIndex = Integer.MAX_VALUE;
        return input.substring(min(min(spaceIndex, parantIndex), input.length())).trim();
    }

    private String create(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();

        String columnsParanthesis = input.substring(input.indexOf("(") + 1, input.indexOf(")")).trim();
        String[] tokens = columnsParanthesis.split(",");
        ColumnInfo[] columnInfos = new ColumnInfo[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            columnInfos[i] = new ColumnInfo();
            tokens[i] = tokens[i].trim();
            String[] sub = tokens[i].split(" +");
            columnInfos[i].name = sub[0];
            columnInfos[i].type = sub[1].equals("INT") ? ColumnInfo.Type.INT : ColumnInfo.Type.STRING;
        }

        String pk = null;
        input = input.substring(input.indexOf(")") + 1).trim();
        input = input.replace(';', ' ');
        if(input.contains("PRIMARY KEY")) {
            input = deleteNextWord(deleteNextWord(input));
            pk = getNextWord(input);
            input = deleteNextWord(input);
        }

        ArrayList<ForeignKey> fks = new ArrayList<>();
        while (input.contains("FOREIGN KEY")) {
            ForeignKey fk = new ForeignKey(); // | Foreign key
            input = deleteNextWord(deleteNextWord(input));
            fk.info = findColumnInfo(columnInfos,getNextWord(input)); // Foreign key | xxx refre...
            input = deleteNextWord(deleteNextWord(input));
            fk.referToTable = Table.getTable(getNextWord(input)); // Refrences | yyy on delete ### on update
            input = deleteNextWord(deleteNextWord(deleteNextWord(input)));
            fk.onDelete = getNextWord(input).equals("CASCADE") ? ForeignKey.Option.CASCADE : ForeignKey.Option.RESTRICT;
            input = deleteNextWord(deleteNextWord(deleteNextWord(input)));
            fk.onUpdate = getNextWord(input).equals("CASCADE") ? ForeignKey.Option.CASCADE : ForeignKey.Option.RESTRICT;
            input = deleteNextWord(input);
            fks.add(fk);
        }

        Table.create(name, columnInfos, findColumnInfo(columnInfos, pk), fks);
        System.out.println("TABLE CREATED");
        return "TABLE CREATED";
    }

    public void reset() {
        Table.clear();
    }
}