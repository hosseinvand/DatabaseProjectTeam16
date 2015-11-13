package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


public class Table {
    private String name;
    private ColumnInfo[] columns;
    private ArrayList<Row> rows;
    private Map<String, Multimap<Object, Row>> indexMaps;
    private static Map<String, Table> tables = new HashMap<String, Table>();

    public String getName()
    {
        return name;
    }

    public ArrayList<Row> getRows()
    {
        return rows;
    }

    public ColumnInfo[] getColumns()
    {
        return columns;
    }

    public Multimap<Object, Row> getIndexMap(String columnName)
    {
        return indexMaps.get(columnName);
    }

    public boolean isIndexed(String columnName)
    {
        return indexMaps.containsKey(columnName);
    }

    public static Table getTable(String name)
    {
        return tables.get(name);
    }

    private Table(String name, ColumnInfo[] columns)
    {
        this.name = name;
        this.columns = columns;
        indexMaps = new HashMap<>();
        rows = new ArrayList<Row>();
    }

    public static Table create(String name, ColumnInfo[] columns)
    {
        Table newTable = new Table(name, columns);
        tables.put(name, newTable);
        return newTable;
    }

    public Table select(ColumnInfo[] columns, Condition condition)
    {
        Table selectedTable = new Table("", columns);
        Row[] selectedRows = condition.getValidRows(this);
        Object[] values = new Object[columns.length];
        for(int i=0; i<selectedRows.length; i++)
        {
            for(int j=0; j<columns.length; j++)
                values[j] = selectedRows[i].getValue(columns[j].name);
            Row row = new Row(columns, values);
            selectedTable.insert(row);
        }
        return selectedTable;
    }

    public void insert (Row row)
    {
        rows.add(row);
        Set <String> indexKeySet = indexMaps.keySet();
        Object[] objectArray = indexKeySet.toArray();
        for(int j=0; j<objectArray.length; j++)
        {
            indexMaps.get(objectArray[j].toString()).put(row.getValue(objectArray[j].toString()), row);
        }
        return ;
    }

    public void delete (Condition condition)
    {
        Row[] deleteCandidates = condition.getValidRows(this);
        for(int i=0; i<deleteCandidates.length; i++)
        {
            Set <String> keySet = indexMaps.keySet();
            Object[] objectArray = keySet.toArray();
            for(int j=0; j<objectArray.length; j++)
            {
                indexMaps.get(objectArray[j].toString()).remove(deleteCandidates[i].getValue(objectArray[j].toString()), deleteCandidates[i]);
            }
        }
    }

    public void update (ColumnInfo column, Object value, Condition condition)
    {
        System.out.println(column + " " + value.toString() + " " + condition.toString());
        Row[] updateCandidates = condition.getValidRows(this);
        this.delete(condition);
        for(int i=0; i<updateCandidates.length; i++)
        {
            updateCandidates[i].setValue(column.name, value);
            this.insert(updateCandidates[i]);
        }
    }

    public void createIndex (String indexName, ColumnInfo column)
    {
        // TODO		IndexName Application?
        Multimap<Object, Row> multimap = ArrayListMultimap.create();
        for(int i=0; i<rows.size(); i++)
        {
            multimap.put(rows.get(i).getValue(column.name), rows.get(i));
        }
        indexMaps.put(column.name, multimap);
        System.out.println("INDEX CREATED");
        return ;
    }
}