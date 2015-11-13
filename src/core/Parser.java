package core;

public class Parser {

    final public String VARCHAR = "VARCHAR", INT = "INT";
    public void parse(String input) {
        String s = input;

        if(s.contains("(")){
            String arguments = s.substring(s.indexOf('(')+1, s.indexOf(')'));
            String method = s.substring(0, s.indexOf('('));
            String[] aArguments = arguments.split(",");
            String[] aMethod = method.split(" ");

            if((aMethod[0]+aMethod[1]).equals("CREATETABLE")){
                String tableName = aMethod[2];
                String[] columnName = new String[aArguments.length];
                String[] dataType = new String[aArguments.length];

                for(int i = 0 ; i<aArguments.length ; i++){
                    columnName[i] = aArguments[i].substring(0, aArguments[i].indexOf(" "));
                    dataType[i] = aArguments[i].substring(aArguments[i].indexOf(" ")+1);
                }

                ColumnInfo[] columnInfos = new ColumnInfo[columnName.length];
                for(int i=0; i<columnInfos.length; i++)
                {
                    if(dataType[i] == VARCHAR)	columnInfos[i] = new ColumnInfo(columnName[i], ColumnInfo.Type.STRING);
                    else	columnInfos[i] = new ColumnInfo(columnName[i], ColumnInfo.Type.INT);
                }
                Table.create(tableName, columnInfos);
                System.out.println("TABLE CREATED");
            }

            if((aMethod[0]+aMethod[1]).equals("CREATEINDEX")){
                String indexName = aMethod[2];
                String tableName = aMethod[4];
                String columnName = arguments;
                ColumnInfo columnINFO = new ColumnInfo();
                columnINFO.name = columnName;
                Table.getTable(tableName).createIndex(indexName, columnINFO);
            }

            if((aMethod[0]).equals("INSERT")){
                String tableName = aMethod[2];
                String[] values = aArguments;
                Row temp = new Row(Table.getTable(tableName).getColumns(), values);
                Table.getTable(tableName).insert(temp);
                System.out.println("RECORD INSERTED");
            }
        }
        else{
            String method = s;
            String[] aMethod = method.split(" ");

            if((aMethod[0]).equals("UPDATE")){
                String tableName = aMethod[1];
                String column = aMethod[3].substring(0, aMethod[3].indexOf("="));
                String value = aMethod[3].substring(aMethod[3].indexOf("=")+1);
                String condition = aMethod[5];
                //System.out.println(value);
                ColumnInfo colmninfo = new ColumnInfo();
                colmninfo.name = column;
                Table.getTable(tableName).update(colmninfo, value, Condition.buildCondition(condition));
            }

            if((aMethod[0]).equals("DELETE")){
                String tableName = aMethod[2];
                String condition = aMethod[4];
                Table.getTable(tableName).delete(Condition.buildCondition(condition));
            }

            if((aMethod[0]).equals("SELECT")){
                String tableName = aMethod[3];
                String condition = aMethod[5];
                String[] columns = aMethod[1].split(",");
                ColumnInfo[] columnInfos = new ColumnInfo[columns.length];
                for(int i=0; i<columnInfos.length; i++)
                {
                    for(int j=0; j<Table.getTable(tableName).getColumns().length; j++)
                    {
                        if(Table.getTable(tableName).getColumns()[j].name.equals(columns[i]))
                        {
                            columnInfos[i] = Table.getTable(tableName).getColumns()[j];
                            break;
                        }
                    }
                }
                Table temp = Table.getTable(tableName).select(columnInfos, Condition.buildCondition(condition));

                for(int i=0; i<temp.getColumns().length; i++)
                {
                    System.out.print(temp.getColumns()[i].name);
                    if(i == temp.getColumns().length)	System.out.print("\n");
                    else	System.out.print(",");
                }
                for(int i=0; i<temp.getRows().size(); i++)
                {
                    for(int j=0; j<temp.getColumns().length; j++)
                    {
                        System.out.print(temp.getRows().get(j));
                        if(i == temp.getColumns().length)	System.out.print("\n");
                        else	System.out.print(",");
                    }
                }
            }
        }
    }

}