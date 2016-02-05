package core;

import core.conditions.Agg.HavingCondition;
import core.conditions.Agg.TrueHavingCondition;

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
        else if (input.indexOf("CREATE VIEW") == 0)
            return view(input.substring("CREATE VIEW".length()));
        return null;
    }

    private String view(String input) {
        String name = getNextWord(input);
        input = deleteNextWord(deleteNextWord(deleteNextWord(input)));
        View view = new View(input, name);
        System.out.println("VIEW CREATED");
        return "VIEW CREATED";
    }

    private String delete(String input) {
        input = input.trim();
        String name = getNextWord(input);
        input = deleteNextWord(input);
        input = deleteNextWord(input);
        input = input.trim();
        String expression = input.substring(0, input.length() - 1).trim();
        if(!Table.isTable(name) && !View.getView(name).isUpdatable()) {
            System.out.println("VIEW " + name + " IS NOT UPDATABLE");
            return "VIEW" + name + "IS NOT UPDATABLE";
        }
        Table table;
        if(!Table.isTable(name)) {
            table = View.getView(name).getInnerTable();
        }
        else
            table = Table.getTable(name);
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
        if(!Table.isTable(name) && !View.getView(name).isUpdatable()) {
            System.out.println("VIEW " + name + " IS NOT UPDATABLE");
            return "VIEW" + name + "IS NOT UPDATABLE";
        }
        Table table = Table.getTable(name);
        if(!Table.isTable(name)) {
            table = View.getView(name).getInnerTable();
        }
        else
            table = Table.getTable(name);
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
        if(!Table.isTable(name) && !View.getView(name).isUpdatable()) {
            System.out.println("VIEW " + name + " IS NOT UPDATABLE");
            return "VIEW" + name + "IS NOT UPDATABLE";
        }
        Table table;
        ColumnInfo[] columnInfosView = null;
        if(!Table.isTable(name)) {
            table = View.getView(name).getInnerTable();
            columnInfosView = View.getTable(name).getColumns();
        }
        else
            table = Table.getTable(name);
        ColumnInfo[] columnInfos = table.getColumns();
        String[] values = new String[columnInfos.length];
        int i = 0;
        int j = -1;
        while((columnInfosView==null || i<columnInfosView.length) && (j+1)<columnInfos.length) {
            j++;
            if(columnInfosView != null && notContains(columnInfosView, columnInfos[j].name)) {
                values[j] = null;
                continue;
            }
//            System.out.println(columnInfos[j].toString()+" " + tokens[i]);
            if (columnInfos[j].type == ColumnInfo.Type.INT)
                values[j] = tokens[i].trim();
            else
                values[j] = tokens[i].trim().replace("\"", "");
            i++;
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

    private boolean notContains(ColumnInfo[] columnInfosView, String name) {
        for(ColumnInfo columnInfo : columnInfosView) {
            if(columnInfo.name.equals(name))
                return false;
        }
        return true;
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

    private static ColumnInfo findColumnInfo(Table table, String colName) {
        ColumnInfo[] columnInfos = table.getColumns();
        for (int i = 0; i < columnInfos.length; i++) {
            if (columnInfos[i].name.equals(colName))
                return columnInfos[i];
        }
        return null;
    }

    private static ColumnInfo findColumnInfo(ColumnInfo[] columnInfos, String colName) {
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
        boolean join = false;
        if(from.contains(",")) {
            join = true;
            table = Table.product(Table.getTable(from.split(",")[0].trim()), Table.getTable(from.split(",")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else if(from.contains("JOIN")) {
            join = true;
            table = Table.join(Table.getTable(from.split("JOIN")[0].trim()), Table.getTable(from.split("JOIN")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else {
            table = Table.getTable(from.trim());
        }
        input = input.substring(input.indexOf("WHERE"));
        input = deleteNextWord(input);
        String conditionExpression = input.substring(0, input.contains("GROUP") ? input.indexOf("GROUP") : input.length()-1).trim();
        ArrayList<ColumnInfo> list = new ArrayList<>();
        for (int i = 0; i < colNames.length; i++) {
            list.add(findColumnInfo(table, colNames[i]));
        }
        Table selected;
        if(input.contains("GROUP")) {
            input = input.substring(input.indexOf("GROUP"));
            input = deleteNextWord(deleteNextWord(input));
            String[] grpTokens = input.substring(0, input.contains("HAVING") ? input.indexOf("HAVING") : input.length()-1).trim().split(",");
            String[] grpColNames = new String[grpTokens.length];
            ArrayList<ColumnInfo> grplist = new ArrayList<>();
            for (int i = 0; i < grpTokens.length; i++) {
                grpColNames[i] = join ? table.getFullName(grpTokens[i].trim()) : grpTokens[i].trim();
            }
            for (int i = 0; i < grpColNames.length; i++) {
                grplist.add(findColumnInfo(table, grpColNames[i]));
            }

            HavingCondition cond = new TrueHavingCondition();
            if(input.contains("HAVING")) {
                input = input.substring(input.indexOf("HAVING"));
                input = deleteNextWord(input);
                cond = new HavingCondition(input.substring(0, input.length()-1));
            }
            selected = table.selectWithGroup(list.toArray(new ColumnInfo[list.size()]), Condition.buildCondition(conditionExpression),
                    grplist.toArray(new ColumnInfo[grpColNames.length]), cond);
        } else {
            selected = table.select(list.toArray(new ColumnInfo[list.size()]), Condition.buildCondition(conditionExpression));
        }
        if (selected.getRows().size() == 0) {
            System.out.println("NO RESULTS");
            return "NO RESULTS";
        }
        else {
            System.out.print(selected.toString());
            return selected.toString();
        }
    }

    private static String getNextWord(String input) {
        input = input.trim();
        int spaceIndex = input.indexOf(' ');
        if (spaceIndex == -1)
            spaceIndex = Integer.MAX_VALUE;
        int parantIndex = input.indexOf('(');
        if (parantIndex == -1)
            parantIndex = Integer.MAX_VALUE;
        return input.substring(0, min(min(spaceIndex, parantIndex), input.length())).trim();
    }

    private static String deleteNextWord(String input) {
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

    public static Table blindSelect(String input) {
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
        boolean join = false;
        if(from.contains(",")) {
            join = true;
            table = Table.product(Table.getTable(from.split(",")[0].trim()), Table.getTable(from.split(",")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else if(from.contains("JOIN")) {
            join = true;
            table = Table.join(Table.getTable(from.split("JOIN")[0].trim()), Table.getTable(from.split("JOIN")[1].trim()));
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = table.getFullName(colNames[i]);
            }
        } else {
            table = Table.getTable(from.trim());
        }
        input = input.substring(input.indexOf("WHERE"));
        input = deleteNextWord(input);
        String conditionExpression = input.substring(0, input.contains("GROUP") ? input.indexOf("GROUP") : input.length()-1).trim();
        ArrayList<ColumnInfo> list = new ArrayList<>();
        for (int i = 0; i < colNames.length; i++) {
            list.add(findColumnInfo(table, colNames[i]));
        }
        Table selected;
        if(input.contains("GROUP")) {
            input = input.substring(input.indexOf("GROUP"));
            input = deleteNextWord(deleteNextWord(input));
            String[] grpTokens = input.substring(0, input.contains("HAVING") ? input.indexOf("HAVING") : input.length()-1).trim().split(",");
            String[] grpColNames = new String[grpTokens.length];
            ArrayList<ColumnInfo> grplist = new ArrayList<>();
            for (int i = 0; i < grpTokens.length; i++) {
                grpColNames[i] = join ? table.getFullName(grpTokens[i].trim()) : grpTokens[i].trim();
            }
            for (int i = 0; i < grpColNames.length; i++) {
                grplist.add(findColumnInfo(table, grpColNames[i]));
            }

            HavingCondition cond = new TrueHavingCondition();
            if(input.contains("HAVING")) {
                input = input.substring(input.indexOf("HAVING"));
                input = deleteNextWord(input);
                cond = new HavingCondition(input.substring(0, input.length()-1));
            }
            selected = table.selectWithGroup(list.toArray(new ColumnInfo[list.size()]), Condition.buildCondition(conditionExpression),
                    grplist.toArray(new ColumnInfo[grpColNames.length]), cond);
        } else {
            selected = table.select(list.toArray(new ColumnInfo[list.size()]), Condition.buildCondition(conditionExpression));
        }
        return selected;
    }

    public void reset() {
        Table.clear();
    }
}