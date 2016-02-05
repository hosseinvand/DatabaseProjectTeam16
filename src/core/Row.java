package core;

import java.util.ArrayList;
import java.util.HashMap;

import static core.Table.getEffectiveName;

public class Row {
    private HashMap<String, String> valueMap;
    private HashMap<String, ColumnInfo.Type> typeMap;
    private HashMap<String, String> effectiveValueMap;
    private HashMap<String, ColumnInfo.Type> effectiveTypeMap;

    public String getValue(String columnName) {
        String ret;
        if(valueMap.containsKey(columnName))
            ret = valueMap.get(columnName);
        else
            ret = effectiveValueMap.get(columnName);
        if(ret == null)
            return "NULL";
        else
            return ret;
    }

    public ColumnInfo.Type getType(String columnName) {
        if(typeMap.containsKey(columnName))
            return typeMap.get(columnName);
        else
            return effectiveTypeMap.get(columnName);
    }

    public void setValue(String columnName, String value) {
        valueMap.put(columnName, value);
        effectiveValueMap.put(getEffectiveName(columnName), value);
    }

    public Row(ColumnInfo[] columns, String[] values) {
        this.valueMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.effectiveTypeMap = new HashMap<>();
        this.effectiveValueMap = new HashMap<>();
        for (int i = 0; i < columns.length; ++i) {
            valueMap.put(columns[i].name, values[i]);
            typeMap.put(columns[i].name, columns[i].type);
            effectiveValueMap.put(getEffectiveName(columns[i].name), values[i]);
            effectiveTypeMap.put(getEffectiveName(columns[i].name), columns[i].type);
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
                "valueMap=" + valueMap +
                ", typeMap=" + typeMap +
                '}';
    }


    public void checkC2(ArrayList<ForeignKey> fks) throws Table.DBException {
        for(ForeignKey fk : fks)
            checkC2(fk);
    }

    public void checkC2(ForeignKey fk) throws Table.DBException {
        if(fk.referToTable.getRowByPK(this.getValue(fk.info.name)) == null)
            throw Table.DBException.c2Exception();
    }
}