package core;

public class ReverseForeignKey {
    public ColumnInfo info;
    public ForeignKey.Option onUpdate;
    public ForeignKey.Option onDelete;
    public Table table;

    public ReverseForeignKey(ColumnInfo info, ForeignKey.Option onUpdate, ForeignKey.Option onDelete, Table table) {
        this.info = info;
        this.onUpdate = onUpdate;
        this.onDelete = onDelete;
        this.table = table;
    }

}
