package core;

import java.util.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import core.conditions.Agg.HavingCondition;


public class Table {
    private String name;
    private ColumnInfo[] columns;
    private ArrayList<Row> rows;
    private Map<String, Multimap<String, Row>> indexMaps;
    private static Map<String, Table> tables = new HashMap<String, Table>();
    private ColumnInfo pk;
    private ArrayList<ForeignKey> fks;
    private ArrayList<ReverseForeignKey> rfks;

    public String getName() {
        return name;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public ColumnInfo[] getColumns() {
        return columns;
    }

    public Multimap<String, Row> getIndexMap(String columnName) {
        return indexMaps.get(columnName);
    }

    public boolean isIndexed(String columnName) {
        return indexMaps.containsKey(columnName);
    }

    public static Table getTable(String name) {
        if(!tables.containsKey(name)) {
            return View.getTable(name);
        }
        return tables.get(name);
    }

    public static boolean isTable(String name) {
        return tables.containsKey(name);
    }

    private Table(String name, ColumnInfo[] columns) {
        this.name = name;
        this.columns = columns;
        indexMaps = new HashMap<>();
        rows = new ArrayList<Row>();
        this.fks = new ArrayList<>();
        this.rfks = new ArrayList<>();
        this.pk = null;
    }

    public static Table create(String name, ColumnInfo[] columns, ColumnInfo pk, ArrayList<ForeignKey> fks) {
        Table newTable = new Table(name, columns);
        newTable.pk = pk;
        newTable.fks = fks;
        for(ForeignKey fk : fks) {
            fk.referToTable.addReverseForeignKey(new ReverseForeignKey(fk.info, fk.onUpdate, fk.onDelete, newTable));
        }
        tables.put(name, newTable);
        if(pk != null)
            newTable.createIndex(null, pk);
        for(ForeignKey fk : fks) {
            newTable.createIndex(null, fk.info);
        }
        return newTable;
    }

    private void addReverseForeignKey(ReverseForeignKey reverseForeignKey) {
        rfks.add(reverseForeignKey);
    }


    public ColumnInfo getPk() {
        return pk;
    }

    public ArrayList<ForeignKey> getFks() {
        return fks;
    }

    public static Table product(Table left, Table right) {
        ColumnInfo[] productColumns = new ColumnInfo[left.columns.length + right.columns.length];
        for (int i = 0; i < left.columns.length; i++) {
            productColumns[i] = new ColumnInfo(left.getName() + "." + left.columns[i].name, left.columns[i].type);
        }
        for (int i = 0; i < right.columns.length; i++) {
            productColumns[left.columns.length + i] = new ColumnInfo(right.getName() + "." + right.columns[i].name,
                    right.columns[i].type);
        }

        Table table = new Table(left.getName()+","+right.getName(), productColumns);

        String[] values = new String[productColumns.length];
        for (int i = 0; i < left.getRows().size(); i++) {
            Row lrow = left.getRows().get(i);
            for (int k = 0; k < left.columns.length; k++) {
                values[k] = lrow.getValue(getEffectiveName(productColumns[k].name));
            }
            for (int j = 0; j < right.getRows().size(); j++) {
                Row rrow = right.getRows().get(j);
                for (int k = 0; k < right.columns.length; k++) {
                    values[left.columns.length + k] = rrow.getValue(getEffectiveName(productColumns[k + left.columns.length].name));
                }
                table.blindInsert(new Row(productColumns, values));
            }
        }

        return table;
    }

    public static String getEffectiveName(String name) {
        if(name.contains("."))
            return name.split("\\.")[1];
        return name;
    }

    public static Table join(Table left, Table right) {
        for (ForeignKey fk : left.getFks()) {
            if(fk.referToTable == right) {
                return joinWith(left, right, fk.info);
            }
        }

        for (ForeignKey fk : right.getFks()) {
            if(fk.referToTable == left) {
                return joinWith(right, left, fk.info);
            }
        }
        return null;
    }

    private static Table joinWith(Table left, Table right, ColumnInfo dealer) {
        ColumnInfo[] joinColumns = new ColumnInfo[left.columns.length + right.columns.length];
        for (int i = 0; i < left.columns.length; i++) {
            joinColumns[i] = new ColumnInfo(left.getName() + "." + left.columns[i].name, left.columns[i].type);
        }
        for (int i = 0; i < right.columns.length; i++) {
            joinColumns[left.columns.length + i] = new ColumnInfo(right.getName() + "." + right.columns[i].name,
                    right.columns[i].type);
        }

        Table table = new Table(left.getName()+","+right.getName(), joinColumns);

        String[] values = new String[joinColumns.length];
        for (int i = 0; i < left.getRows().size(); i++) {
            Row lrow = left.getRows().get(i);
            for (int k = 0; k < left.columns.length; k++) {
                values[k] = lrow.getValue(getEffectiveName(joinColumns[k].name));
            }
            Row rrow = right.getRowByPK(lrow.getValue(dealer.name));
            for (int k = 0; k < right.columns.length; k++) {
                values[left.columns.length + k] = rrow.getValue(getEffectiveName(joinColumns[k + left.columns.length].name));
            }
            table.blindInsert(new Row(joinColumns, values));
        }

        return table;
    }

    public Table select(ColumnInfo[] columns, Condition condition) {
        Table selectedTable = new Table("", columns);
        Row[] selectedRows = condition.getValidRows(this);
        String[] values = new String[columns.length];
        for (int i = 0; i < selectedRows.length; i++) {
            for (int j = 0; j < columns.length; j++) {
                values[j] = selectedRows[i].getValue(columns[j].name);
            }
            Row row = new Row(columns, values);
            try {
                selectedTable.insert(row);
            } catch (DBException e) {
                //not gonna happen, bad design :(
            }
        }
        return selectedTable;
    }

    public Table selectWithGroup(ColumnInfo[] columns, Condition condition, ColumnInfo[] group, HavingCondition cond) {
        Table selectedTable = new Table("", columns);
        Row[] selectedRows = condition.getValidRows(this);
        Row[] groupPart= projectAndUnique(selectedRows, group);
        ArrayList<Row> validGroups = new ArrayList<>();
        for (int i = 0; i < groupPart.length; i++) {
            ArrayList<Row> groupRows = new ArrayList<>();
            for (int j = 0; j < selectedRows.length; j++) {
                if(subset(groupPart[i], group, selectedRows[j]))
                    groupRows.add(selectedRows[j]);
            }
            if(cond.isValid(groupRows))
                validGroups.add(groupPart[i]);
        }
        Row[] finalRows = project(validGroups.toArray(new Row[validGroups.size()]), columns);
        for (int i = 0; i < finalRows.length; i++) {
            selectedTable.blindInsert(finalRows[i]);
        }
        return selectedTable;
    }

    private boolean subset(Row small, ColumnInfo[] smallInfo, Row big) {
        for (int i = 0; i < smallInfo.length; i++) {
            if(!small.getValue(smallInfo[i].name).equals(big.getValue(smallInfo[i].name)))
                return false;
        }
        return true;
    }

    private Row[] projectAndUnique(Row[] selectedRows, ColumnInfo[] group) {
        Row[] projectRow = project(selectedRows, group);
        Set<Row> tmpSet = new HashSet<Row>(Arrays.asList(projectRow));
        Row[] unique = tmpSet.toArray(new Row[tmpSet.size()]);
        return unique;
    }

    private Row[] project(Row[] selectedRows, ColumnInfo[] group) {
        Row[] projectRow = new Row[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            String values[] = new String[group.length];
            for (int j = 0; j < group.length; j++) {
                values[j] = selectedRows[i].getValue(group[j].name);
            }
            projectRow[i] = new Row(group, values);
        }
        return projectRow;
    }

    public void insert(Row row) throws DBException {
        checkC1ForInsert(row);
        row.checkC2(fks);
        rows.add(row);
        Set<String> indexKeySet = indexMaps.keySet();
        Object[] objectArray = indexKeySet.toArray();
        for (int j = 0; j < objectArray.length; j++) {
            indexMaps.get(objectArray[j].toString()).put(row.getValue(objectArray[j].toString()), row);
        }
        return;
    }

    public void blindInsert(Row row) {
        rows.add(row);
        Set<String> indexKeySet = indexMaps.keySet();
        Object[] objectArray = indexKeySet.toArray();
        for (int j = 0; j < objectArray.length; j++) {
            indexMaps.get(objectArray[j].toString()).put(row.getValue(objectArray[j].toString()), row);
        }
        return;
    }

    private void checkC1ForInsert(Row row) throws DBException {
        if(this.pk == null)
            return;
        if(getRowByPK(row.getValue(pk.name)) != null)
            throw DBException.c1Exception();
    }

    public Row getRowByPK(String value) {
        if(pk == null)
            return null;
        Collection<Row> result = indexMaps.get(pk.name).get(value);
        if(result == null || result.size() == 0)
            return null;
        else return result.toArray(new Row[result.size()])[0];
    }

    public void delete(Condition condition) throws DBException {
        Row[] deleteCandidates = condition.getValidRows(this);
        for(Row row : deleteCandidates) {
            if(canDelete(row) == false)
                throw DBException.RestrictException();
        }
        for(Row row : deleteCandidates) {
            delete(row);
        }
    }

    private void delete(Row row) {
        blindDelete(row);

        if(pk == null)
            return;

        for(ReverseForeignKey rfk : rfks) {
            if(rfk.onDelete == ForeignKey.Option.RESTRICT)
                continue;

            Row[] children = rfk.table.getRowsByFK(rfk.info, row.getValue(pk.name));
            for(Row child : children) {
                rfk.table.delete(child);
            }
        }
    }

    private boolean canDelete(Row row) {
        if(pk == null)
            return true;
        for(ReverseForeignKey rfk : rfks) {
            if(rfk.onDelete != ForeignKey.Option.RESTRICT)
                continue;
            if(rfk.table.getRowsByFK(rfk.info, row.getValue(pk.name)).length > 0)
                return false;
        }

        for(ReverseForeignKey rfk : rfks) {
            if(rfk.onDelete == ForeignKey.Option.RESTRICT)
                continue;

            Row[] children = rfk.table.getRowsByFK(rfk.info, row.getValue(pk.name));
            for(Row child : children) {
                if(rfk.table.canDelete(child) == false)
                    return false;
            }
        }

        return true;
    }

    private Row[] getRowsByFK(ColumnInfo info, String value) {
        Collection<Row> result = indexMaps.get(info.name).get(value);
        if(result == null)
            return new Row[0];
        return result.toArray(new Row[result.size()]);
    }


    public void update(ColumnInfo column, String expression, Condition condition) throws DBException {
        Row[] updateCandidates = condition.getValidRows(this);
        ComputeValue computeValue = new ComputeValue(expression);

        if(pk != null && column.name.equals(pk.name)) {
            //check for restriction
            for(Row row : updateCandidates) {
                for (ReverseForeignKey rfk : rfks) {
                    if (rfk.onUpdate != ForeignKey.Option.RESTRICT)
                        continue;
                    if (rfk.table.getRowsByFK(rfk.info, row.getValue(pk.name)).length > 0)
                        throw DBException.RestrictException();
                }
            }

            //update and cascade
            for (Row row : updateCandidates) {
                blindDelete(row);
                String oldValue = row.getValue(column.name);
                row.setValue(column.name, computeValue.getValue(row));
                try {
                    checkC1ForInsert(row);
                } catch (DBException e) {
                    row.setValue(column.name, oldValue);
                    blindInsert(row);
                    throw e;
                }
                blindInsert(row);
                for(ReverseForeignKey rfk : rfks) {
                    if(rfk.onUpdate == ForeignKey.Option.RESTRICT)
                        continue;

                    Row[] children = rfk.table.getRowsByFK(rfk.info, oldValue);
                    for(Row child : children) {
                        rfk.table.blindUpdate(child, rfk.info, computeValue.getValue(row));
                    }
                }
            }

            return;
        }

        //check for c2
        for(Row row : updateCandidates) {
            String oldValue = row.getValue(column.name);
            row.setValue(column.name, computeValue.getValue(row));
            try {
                row.checkC2(fks);
            }catch (DBException e) {
                row.setValue(column.name, oldValue);
                throw e;
            }
            row.setValue(column.name, oldValue);
        }

        for(Row row : updateCandidates) {
            blindDelete(row);
            row.setValue(column.name, computeValue.getValue(row));
            blindInsert(row);
        }
    }

    private void blindUpdate(Row row, ColumnInfo info, String value) {
        blindDelete(row);
        row.setValue(info.name, value);
        blindInsert(row);
    }

    private void blindDelete(Row row) {
        rows.remove(row);
        Set<String> keySet = indexMaps.keySet();
        Object[] objectArray = keySet.toArray();
        for (int j = 0; j < objectArray.length; j++) {
            indexMaps.get(objectArray[j].toString()).remove(row.getValue(objectArray[j].toString()), row);
        }
    }

    public void createIndex(String indexName, ColumnInfo column) {
        Multimap<String, Row> multimap = ArrayListMultimap.create();
        for (int i = 0; i < rows.size(); i++) {
            multimap.put(rows.get(i).getValue(column.name), rows.get(i));
        }
        indexMaps.put(column.name, multimap);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < columns.length; i++) {
            result += getEffectiveName(columns[i].name);
            if (i < columns.length - 1)
                result += ",";
            else
                result += "\n";
        }
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            for (int j = 0; j < columns.length; j++) {
                result += row.getValue(columns[j].name);
                if (j < columns.length - 1)
                    result += ",";
                else
                    result += "\n";
            }
        }
        return result;
    }

    public static void clear() {
        tables.clear();
    }

    public String getFullName(String name) {
        if(name.contains("."))
            return name;
        for (int i = 0; i < columns.length; i++) {
            if(getEffectiveName(columns[i].name).equals(name))
                return columns[i].name;
        }
        return name;
    }

    public static class DBException extends Exception {
        public DBException(String message) {
            super(message);
        }

        public static DBException c1Exception() {
            return new DBException("C1 CONSTRAINT FAILED");
        }

        public static DBException c2Exception() {
            return new DBException("C2 CONSTRAINT FAILED");
        }

        public static DBException RestrictException() {
            return new DBException("FOREIGN KEY CONSTRAINT RESTRICTS");
        }
    }
}