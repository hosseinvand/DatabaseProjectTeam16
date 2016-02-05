package core;

import java.util.HashMap;
import java.util.Map;

public class View {
    public String selectExpr;
    public String name;
    public static Map<String, String> nameToSelect = new HashMap<>();
    public static Map<String, View> nameToView = new HashMap<>();

    public View(String selectExpr, String name) {
        this.selectExpr = selectExpr;
        this.name = name;
        nameToSelect.put(name, selectExpr);
        nameToView.put(name, this);
    }

    public Table getInnerTable() {
        String tables = selectExpr.substring(selectExpr.indexOf("FROM")+4, selectExpr.indexOf("WHERE")).trim();
        if(!Table.isTable(tables))
            return nameToView.get(tables).getInnerTable();
        return Table.getTable(tables);
    }

    public boolean isUpdatable() {
        if(selectExpr.contains("GROUP BY") || selectExpr.contains("HAVING"))
            return false;
        if(selectExpr.substring(selectExpr.indexOf("WHERE")).contains("FROM"))
            return false;
        if(selectExpr.contains("JOIN"))
            return false;
        String tables = selectExpr.substring(selectExpr.indexOf("FROM")+4, selectExpr.indexOf("WHERE")).trim();
        if(tables.contains(","))
            return false;
        if(!Table.isTable(tables) && !nameToView.get(tables).isUpdatable())
            return false;
        if(!selectExpr.substring(0, selectExpr.indexOf("FROM")).contains(getInnerTable().getPk().name))
            return false;
        return true;
    }

    public static View getView(String name) {
        return nameToView.get(name);
    }

    public static Table getTable(String name) {
        return Parser.blindSelect(nameToSelect.get(name));
    }
}
