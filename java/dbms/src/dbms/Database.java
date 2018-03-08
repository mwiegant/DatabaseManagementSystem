package dbms;

import java.util.Vector;
import javafx.util.Pair;

public class Database {
	
	Database()
	{
		
	}
	
	boolean createTable(String name)
	{
		return true;
	}

	boolean createTable(String name, Vector<Pair<String, String>> columnInfo)
	{
		return true;
	}
	
	boolean dropTable(String name)
	{
		return true;
	}
	
	boolean getTable(String name, Table table)
	{
		return true;
	}
}
