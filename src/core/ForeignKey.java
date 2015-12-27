package core;

public class ForeignKey {
    public ColumnInfo info;
    public Option onUpdate;
    public Option onDelete;
    public Table referToTable;

    public enum Option {
        CASCADE, RESTRICT;
    }

    @Override
    public String toString() {
        return "ForeignKey{" +
                "info=" + info.name +
                ", onUpdate=" + onUpdate +
                ", onDelete=" + onDelete +
                ", referToTable=" + referToTable.getName() +
                '}';
    }
}
