package dbms;

import java.util.HashMap;
import java.util.Map;

public class Table {
	
	Table()
	{
		
	}
	
	Table(String tableName)
	{
		
	}
	
	Map<String, String> getColumns()
	{
		return new HashMap<String, String>();
	}
	
	String getTableName()
	{
		return "haha";
	}
	
	boolean createColumn(String colName, String colType)
	{
		return true;
	}
	
	boolean deleteColumn(String colName)
	{
		return true;
	}
}
