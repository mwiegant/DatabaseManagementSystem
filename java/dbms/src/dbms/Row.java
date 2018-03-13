package dbms;

import java.util.HashMap;
import java.util.Map;

public class Row {

	Map<String, Object> data = new HashMap<String, Object>();
	
	Row() {
		// nothing to do here
	}
	
	/*
	 * Adds a new column of data into this row. Fails to add the new column if one with the same name
	 * already exixts in this row.
	 */
	public boolean addData(String columnName, String columnType, Object newData) {
		
		// cannot add data for a column that already has a value in this row
		if (data.containsKey(columnName))
			return false;
		
		// switch statement
		
		// TODO
		return true;
	}
	
}
