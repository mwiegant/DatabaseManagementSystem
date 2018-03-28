package dbms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javafx.util.Pair;

public class Database {
	
	private String databaseName;
	private List<Table> tables;
	
	Database(String dbName) {
		databaseName = dbName;
		tables = new ArrayList<Table>();
	}	

	public Database(String dbName, List<Table> theTables)
	{
		databaseName = dbName;
		tables = theTables;
	}
	
	public boolean createTable(String tableName)
	{
		Iterator<Table> it = tables.iterator();
		
		while (it.hasNext()) {
			if (it.next().getTableName().equals(tableName))
				return false;
		}
		
		tables.add(new Table(tableName));
		return true;
	}
	
//	public boolean createTable(string tableName, vector<pair<string, string>> columnInfo)
//	{
//		Table table = new Table(tableName);
//
//		tangible.RefObject<Table> tempRef_table = new tangible.RefObject<Table>(table);
//		if (getTable(tableName, tempRef_table) == false)
//		{
//			table = tempRef_table.argValue;
//			for (int i = 0; i < columnInfo.size(); i++)
//			{
//				table.createColumn(columnInfo[i].first, columnInfo[i].second);
//			}
//
//			tables.push_back(table);
//			return true;
//		}
//		else
//		{
//			table = tempRef_table.argValue;
//		}
//	return false;
//	}

	public Table getTable(String tableName) {
		Iterator<Table> it = tables.iterator();
		Table table = null;
		
		while (it.hasNext()) {
			table = it.next();
			
			if (table.getTableName().equals(tableName))
				return table;
		}
		
		return null;
	}

	public boolean dropTable(String tableName) {
		Iterator<Table> it = tables.iterator();
		Table table = null;
		
		while (it.hasNext()) {
			table = it.next();
			
			if (table.getTableName().equals(tableName)) {
				tables.remove(table);
				return true;
			};
		}
		
		return false;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String dbName) {
		databaseName = dbName;
	}

//	public final boolean updateTable(string tableName, Table table)
//	{
//	for (list<Table>.iterator iterator = tables.begin(), end = tables.end(); iterator != end; iterator++)
//	{
//
//			if (iterator.getTableName().compare(tableName) == 0)
//			{
//				*iterator = table;
//				return true;
//			}
//	}
//	 return false;
//	}

	public final List<String> getTableNames() {				
		List<String> tableNames = new ArrayList<String>();
		Iterator<Table> tablesIt = tables.iterator();
		
		while (tablesIt.hasNext())
			tableNames.add(tablesIt.next().getTableName());
		
		return tableNames;
	}
	
	

	public void addTable(String tableName, Table table) {
		// TODO
	}
	
	public boolean createTable(String name, Vector<Pair<String, String>> columnInfo)
	{
		Table table = new Table();
		table = this.getTable(name);
	
		if (table != null) {
			return false;	
		}
		else {
			this.createTable(name);
			table = getTable(name);
		}
		
		for (Pair<String, String> column : columnInfo)
			table.createColumn(column.getKey() , column.getValue());
		
		return true;
	}	
	
	
	public String getName() {
		return databaseName;
	}
}
