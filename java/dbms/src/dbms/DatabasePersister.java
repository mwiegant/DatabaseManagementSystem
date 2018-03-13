package dbms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabasePersister {
	private List<String> supportedColumnTypes = new ArrayList<String>(Arrays.asList(
			"int", "varchar(20)", "char(20)", "float"));
	
	public Database loadDatabase(String dbCompletePath, String dbName) {
		Database db = new Database(dbName);
		List<String> tableNames = new ArrayList<String>();
		
		// get the list of table names
		getTableNames(dbCompletePath, tableNames);
		
		// iterate through all tables, (NOTE: having no tables is an acceptable scenario)
		for (String tableName : tableNames) {
		
			// load each Table object completely, with data and all
			Table table = loadTable(dbCompletePath, tableName);
			
			db.addTable(tableName, table);		
		}
		
		return db;
	}
	
	public boolean saveDatabase(String dbCompletePath, Database database) {
		List<String> tableNames = database.getTableNames();
		Map<String, String> columns = null;
		Iterator<Row> it = null;
		Table table = null;
		
		try {	
			// make a backup of the database folder, and recreate the new folder
			createDatabaseBackup(dbCompletePath, database.getName());
			
			// iterate through table names, grabbing one table at a time		
			for (String tableName : tableNames) {
			
				// grab the table, its column, and an iterator for the table's data
				table = database.getTable(tableName);
				columns = table.getColumns();
				it = table.getTableData();
				
				// write all the attributes of that table to a file
				saveTable(dbCompletePath, table.getTableName(), columns, it);
			}
			
			// remove the backup folder
			deleteDatabaseBackup(dbCompletePath, database.getName());
			
			// if there hasn't been a failure yet, assume the save was successful
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/*
	 * Initializes a brand-new database, which just means creating the appropriate database folder 
	 */
	public void initializeDatabase(String path) {
		File databaseDirectory = new File(path);
		
		if (!databaseDirectory.exists()) {
			try {
				databaseDirectory.mkdir();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/*
	 * Drops a database, which means deleting the directory (and sub-contents) of the appropriate database
	 */
	public void dropDatabase(String path) {
		deleteDirectory(new File(path));
	}
	
	
	/* 
	 * Recursively deletes all files and directories from a given path, used for dropping databases.
	 * 
	 * modeled code for this function after examples found on: 
	 * http://javarevisited.blogspot.com/2015/03/how-to-delete-directory-in-java-with-files.html 
	 */
	private static boolean deleteDirectory(File path) {
		if (path.isDirectory()) {
			File[] subpaths = path.listFiles();
			for(File subpath : subpaths) {
				boolean succeeded = deleteDirectory(subpath);				
				if (!succeeded)
					return false;
			}
		}
		return path.delete();
	}
	
	
	/*
	 * Grabs all table names from the specified database path.
	 * 
	 * NOTE: Silently ignores any directories found residing at the database path, where tables should be.
	 */
	private void getTableNames(String dbCompletePath, List<String> tableNames) {		
		try {
			File dbFolder = new File(dbCompletePath);
			
			if (dbFolder.exists()) {
				File[] listOfFiles = dbFolder.listFiles();

			    for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						tableNames.add(listOfFiles[i].getName());
					}
			    }
			}
		}
		catch (Exception e) {
			System.out.println(String.format("!Fatal error while grabbing table names."));
			e.printStackTrace();
		}		
	}
	
	
	private Table loadTable(String dbCompletePath, String tableName) {
		Table table = new Table(tableName);
		Map<String, String> columns = new HashMap<String, String>();
		BufferedReader br = null;
		FileReader fr = null;
		String linedata;
		boolean firstLine = true;
		
		try {
			fr = new FileReader(dbCompletePath);
			br = new BufferedReader(fr);
			
			while ((linedata = br.readLine()) != null) {
				// the first line contains data on each of the table's column names/types
				if (firstLine) {
					columns = loadTableColumns(table, linedata);
					firstLine = false;
				}
				// grab the rest of the lines, which have table data, but don't grab any empty lines
				else if (linedata.length() > 0) {
					loadTableRow(table, columns, linedata);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
				
				if (fr != null)
					fr.close();
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return table;
	}
	
	
	/*
	 * Given the table object, and the string representing its columns, loads the columns into the Table.
	 */
	private Map<String, String> loadTableColumns(Table table, String tableColumnLine) {
		String[] columnTokens = tableColumnLine.split("|");
		Map<String, String> columns = new HashMap<String, String>();
		
		for (String column : columnTokens) {
			String[] tokens = column.split(" ");	// col_name col_type
			
			// check if the column type is a supported one, before adding it to the table
			if (supportedColumnTypes.contains(tokens[1])) {
				table.createColumn(tokens[0], tokens[1]);
				columns.put(tokens[0], tokens[1]);
			}
			else {
				System.out.println(String.format("Error - unsupported column type found in %1$s: %2$s", 
									table.getTableName(), tokens[1]));
			}			
		}
		return columns;
	}
	
	private void loadTableRow(Table table, Map<String, String> columns, String tableRowLine) {
		String[] data = tableRowLine.split("|");
		String[] colNames = (String[]) columns.keySet().toArray();
		List<String> colTypes = (List<String>) columns.values();
		Row row = new Row();
		
		// there is technically a possibility of having more or less data elements than columns...
		int numColumns = data.length;
		
		try {
			// add each column to the Row object
			for (int i = 0; i < numColumns; i++) {			
				if (!row.addData(colNames[i], colTypes.get(i), data[i]))
					System.out.println(
						String.format("Error while adding column of data to a tablerow in table %1$s.", 
							table.getTableName()));							
			}
			
			// and finally, add the Row object to the table
			table.addRow(row);
		}
		catch (Exception e) {
			
		}		
	}
	
	private void createDatabaseBackup(String dbCompletePath, String databaseName) {

	
		
		// dbCompletePath DOES come with the database name appended to the end of the path
		
		
		
		
		
		// build the path for the backup database (build a hacky solution for building the windows/unix file paths)
		// one possibility: do a replace of the database name with 'database_name_old', in the dbCompletePath
		
		// rename the entire directory (https://coderanch.com/t/369445/java/Rename-directory-Java)
		
		// remake the old directory, File.mkdir()
		
	}
	
	private void saveTable(String dbPath, String tableName, Map<String, String> columns, Iterator<Row> it) {
		
		
		
		
		// use the dbPath and tableName to build the filepath
		
		// then insert the columns into the first line of the new file
		
		// then insert each row, one at a time
	}
	
	
	private void deleteDatabaseBackup(String dbCompletePath, String databaseName) {
		
		
		
		
		
		
		// do a replace of the database name with 'database_name_old', in the dbCompletePath
		
		// .....and delete that directory (using the private recursive function deleteDirectory() )
		
	}
}
