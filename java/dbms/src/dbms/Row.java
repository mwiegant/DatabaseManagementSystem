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
	 * already exists in this row.
	 */
	public boolean addData(String columnName, String columnType, String newData) {
		
		// cannot add data for a column that already has a value in this row
		if (data.containsKey(columnName))
			return false;
		
		// add the column to the data
		switch (columnType) {
		case "int":
			data.put(columnName, Integer.parseInt(newData));
			break;
			
		case "float":
			data.put(columnName, Float.parseFloat(newData));
			break;
		
		case "varchar(20)":
		case "char(20)":
			data.put(columnName, newData);
			break;
			
		default:
			System.out.println("!Error - invalid datatype: " + columnType);
		}
		
		return true;
	}
	
	public Object getData(String columnName) {
		if (data.containsKey(columnName))
			return data.get(columnName);
		return null;
	}
}
