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
    private Map<String, Multimap<Object, Row>> indexMaps = new HashMap<String, Multimap<Object,Row>>();
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
        rows = new ArrayList<Row>();
        tables.put(name, this);
    }

    public static Table create(String name, ColumnInfo[] columns)
    {
        Table temp = new Table(name, columns);
        return temp;
    }

    public Table select(ColumnInfo[] columns, Condition condition)
    {
        Table temp = Table.create("", columns);
        tables.remove(temp);
        Row[] tep = condition.getValidRows(this);
        Object[] values = new Object[columns.length];
        for(int i=0; i<tep.length; i++)
        {
            for(int j=0; j<columns.length; j++)		values[j] = tep[i].getValue(columns[j].name);
            Row row = new Row(columns, values);
            temp.insert(row);
        }
        return temp;
    }

    public void insert (Row row)
    {
        rows.add(row);
        Set <String> tep = indexMaps.keySet();
        Object[] s = tep.toArray();
        for(int j=0; j<s.length; j++)
        {
            indexMaps.get(s[j].toString()).put(row.getValue(s[j].toString()), row);
        }
        return ;
    }

    public void delete (Condition condition)
    {
        Row[] temp = condition.getValidRows(this);
        for(int i=0; i<temp.length; i++)
        {
            Set <String> tep = indexMaps.keySet();
            Object[] s = tep.toArray();
            for(int j=0; j<s.length; j++)
            {
                indexMaps.get(s[j].toString()).remove(temp[i].getValue(s[j].toString()), temp[i]);
            }
        }
    }

    public void update (ColumnInfo column, Object value, Condition condition)
    {
        Row[] temp = condition.getValidRows(this);
        this.delete(condition);
        for(int i=0; i<temp.length; i++)
        {
            temp[i].setValue(column.name, value);
            this.insert(temp[i]);
        }
        return ;
    }

    public void createIndex (String indexName, ColumnInfo column)
    {
        // TODO		index Name chiye ??
        Multimap<Object, Row> temp = ArrayListMultimap.create();
        for(int i=0; i<rows.size(); i++)
        {
            temp.put(rows.get(i).getValue(column.name), rows.get(i));
        }
        indexMaps.put(column.name, temp);
        System.out.println("INDEX CREATED");
        return ;
    }
}