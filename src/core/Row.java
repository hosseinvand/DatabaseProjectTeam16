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
        for (int i = 0; i < columns.length; ++i) {
            valueMap.put(columns[i].name, values[i]);
            typeMap.put(columns[i].name, columns[i].type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (typeMap != null ? !typeMap.equals(row.typeMap) : row.typeMap != null) return false;
        if (valueMap != null ? !valueMap.equals(row.valueMap) : row.valueMap != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = valueMap != null ? valueMap.hashCode() : 0;
        result = 31 * result + (typeMap != null ? typeMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Row{" +
                "valueMap=" + valueMap.toString() +
                ", typeMap=" + typeMap.toString() +
                '}';
    }
}