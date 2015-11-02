package core;

public class Row {
	private Object[] values;
	private final ColumnInfo[] columns;

    public Object getValue(String columnName) {
        return null;
    }
	
	public Row(ColumnInfo[] column, Object[] values) {
		this.columns = column;
		this.values = values;
		//TODO
	}
}
