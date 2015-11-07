package core;

public class ColumnInfo {
	public String name;
	public Type type;

    public ColumnInfo() {

    }

    public ColumnInfo(String name, Type type) {
        this.name = name;
        this.type = type;
    }
	
	public enum Type {
		INT, STRING;
	}
}
