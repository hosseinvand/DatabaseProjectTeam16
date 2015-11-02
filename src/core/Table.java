package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Table {
	private String name;
	private ColumnInfo[] columns;
	private ArrayList<Row> rows;
	private Map<String, Map<Object, Row>> indexMaps;
    private static Map<String, Table> tables = new HashMap<String, Table>();

    public String getName() {
        return name;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public ColumnInfo[] getColumns() {
        return columns;
    }

    public Map<Object, Row> getIndexMap(String columnName) {
        return indexMaps.get(columnName);
    }

    public boolean isIndexed(String columnName) {
        return indexMaps.containsKey(columnName);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    private Table(String name, ColumnInfo[] columns) {
		
	}

    public Table create(String name, ColumnInfo[] columns) {
        return null;
    }
	
	public Table select (ColumnInfo[] columns, Condition condition) {
		return null;
	}
	
	public void insert (Object[] values) {

	}
	
	public void delete (Condition condition) {
		
	}
	
	public void update (ColumnInfo column, Object value, Condition condition) {
		
	}
	
	public void createIndex (String indexName, ColumnInfo column) {
		
	}
}
