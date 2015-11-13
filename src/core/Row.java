package core;

import java.util.HashMap;

public class Row {
    private HashMap<String, Object> valueMap;
    private HashMap<String, ColumnInfo.Type> typeMap;

    public Object getValue(String columnName) {
        return valueMap.get(columnName);
    }

    public ColumnInfo.Type getType(String columnName) {
        return typeMap.get(columnName);
    }

    public void setValue(String columnName, Object value) {
        valueMap.put(columnName, value);
    }
	
	public Row(ColumnInfo[] columns, Object[] values) {
        this.valueMap = new HashMap<String, Object>();
        this.typeMap = new HashMap<String, ColumnInfo.Type>();
        for(int i=0; i<columns.length; ++i) {
            valueMap.put(columns[i].name, values[i]);
            typeMap.put(columns[i].name, columns[i].type);
        }
	}
}