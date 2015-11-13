package core;

public class Parser {

	public void parse(String input) {
        /*
		String s = input;
		
		if(s.contains("(")){
			String arguments = s.substring(s.indexOf('(')+1, s.indexOf(')'));
			String method = s.substring(0, s.indexOf('('));
			String[] aArguments = arguments.split(",");
			String[] aMethod = method.split(" ");
			
			if((aMethod[0]+aMethod[1]).equals("CREATETABLE")){
				String tableName = aMethod[2];
				String[] columnName = new String[aArguments.lenght];
				String[] dataType = new String[aArguments.lenght];
				
				for(int i = 0 ; i<aArguments.length ; i++){
					columnNamr[i] = aArguments[i].substring(0, aArguments[i].indexOf(" "));
					dataType[i] = aArguments[i].substring(aArguments[i].indexOf(" ")+1);
				}
			}
			
			if((aMethod[0]+aMethod[1]).equals("CREATEINDEX")){
				String indexName = aMethod[2];
				String tableName = aMethod[4];
				String columnName = arguments;
			}
			
			if((aMethod[0]).equals("INSERT")){
				String tableName = aMethod[2];
				String[] values = aArguments;
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
				System.out.println(value);
			}
			
			if((aMethod[0]).equals("DELETE")){
				String tableName = aMethod[2];
				String condition = aMethod[4];
			}
			
			if((aMethod[0]).equals("SELECT")){
				String tableName = aMethod[3];
				String condition = aMethod[5];
				String[] columns = aMethod[1].split(",");
			}
		}
		*/
	}
	
}
