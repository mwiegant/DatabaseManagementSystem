package dbms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Table {
	private List<Row > rows;
	private String tableName;
	private Map<String, String> columns;
	
	Table()	{
		// initialize the Map of columns data member
		
		// initialize the List<Row> data member
	}
	
	public Table(String tableName) {
		this.tableName = tableName;
		rows = new ArrayList<Row>();
		columns = new LinkedHashMap<String, String>();
	}

	public final Row getRow() {
		Row row = new Row();
		return row;
	}
	
	public final boolean createRow() {
		// temp
		return true;
	}
	
	public final boolean deleteRow()
	{
		// temp
		return true;
	}
	
	public final String getTableName()
	{
		return tableName;
	}
	
	public final Map<String, String> getColumns()
	{
		return columns;
	}	
	
	/*
	 * Creates a new column in this table. Fails to create the column if a column with the same name already exists.
	 */
	public boolean createColumn(String colName, String colType) {
	
		// don't add the column if one with this name already exists
		if (columns.containsKey(colName))
			return false;
		else {
			columns.put(colName, colType);
			return true;
		}		
	}
	
	public final boolean deleteColumn(String colName)
	{
		if (columns.containsKey(colName)) {
			columns.remove(colName);
			return true;
		}
		return false;
	}

	public Iterator<Row> getTableData() {
		return rows.iterator();
	}
	
	public void addRow(Row row) {
		rows.add(row);
	}
	
	public void setTableData(Iterable<Row> data) {
		rows.clear();
		
		for (Row row : data) {
			rows.add(row);
		}
	}
}
