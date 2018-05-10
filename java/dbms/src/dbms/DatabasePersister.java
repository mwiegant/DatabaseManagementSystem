package dbms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
			
			db.addTable(table);		
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
			createDatabaseBackup(dbCompletePath);
			
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
						// add all tables, but do not add the locked tables
						if (!listOfFiles[i].getName().endsWith("_lock"))
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
			fr = new FileReader(dbCompletePath + tableName);
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
		String[] columnTokens = tableColumnLine.split("\\|");
		Map<String, String> columns = new LinkedHashMap<String, String>();
		
		for (String column : columnTokens) {
			String[] tokens = column.trim().split(" ");	// col_name col_type
			
			if (tokens.length == 0)
				break;
			
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
		String[] data = tableRowLine.split(" \\|");
		Object[] colNames = columns.keySet().toArray();
		List<String> colTypes = new ArrayList<String>((Collection<String>) columns.values());
		Row row = new Row();
		
		// there is technically a possibility of having more or less data elements than columns...
		int numColumns = data.length;
		
		try {
			// add each column to the Row object
			for (int i = 0; i < numColumns; i++) {			
				if (!row.addData((String) colNames[i], colTypes.get(i), data[i]))
					System.out.println(
						String.format("Error while adding column of data to a tablerow in table %1$s.", 
							table.getTableName()));							
			}
			
			// and finally, add the Row object to the table
			table.addRow(row);
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void createDatabaseBackup(String dbCompletePath) {
		String completePath = dbCompletePath;
		String backupPath = dbCompletePath + "_old";
	
		File currentDirectory = new File(completePath);
        File backupDirectory = new File(backupPath);
        
        // only make a backup if the database already exists
        if (currentDirectory.exists()) {
        		// rename the directory, then nullify the object
        		currentDirectory.renameTo(backupDirectory);
        		currentDirectory = null;
        		
        		// re-make the old directory
            currentDirectory = new File(backupPath);
            currentDirectory.mkdir();
        }
	}
	
	private void saveTable(String dbPath, String tableName, Map<String, String> columns, Iterator<Row> it) throws FileNotFoundException, UnsupportedEncodingException {
	
		String path = dbPath + tableName;
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		Set<String> columnNames = columns.keySet();
		Iterator<String> columnIt = null;
		boolean newLine = true;
		String data;
		Row row;
		
		try {
			// iterate through all the columns and write each column name and type to the file
			for (String key : columnNames) {
				if (newLine) {
					writer.print(String.format("%1$s %2$s ", key, columns.get(key)));
					newLine = false;
				}
				else
					writer.print(String.format("| %1$s %2$s ", key, columns.get(key)));			
			}
			
			// go to next line
			writer.println();
			newLine = true;
			
			// then insert each row, one at a time
			while (it.hasNext()) {
				row = it.next();
				columnIt = columnNames.iterator();
				
				while (columnIt.hasNext()) {
					data = (String) row.getData(columnIt.next());
					
					if (newLine) {
						writer.print(String.format("%1$s ", data));
						newLine = false;
					}
					else
						writer.print(String.format("| %1$s ", data));
				}
				
				writer.println();
				newLine = true;
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			writer.close();
		}

	}
	
	
	private void deleteDatabaseBackup(String dbCompletePath, String databaseName) {
		String backupPath = dbCompletePath + "_old";
		File backup = new File(backupPath);
		
		// only delete the backup if it exists (won't exist if this is the first time saving this database)
		if (backup.exists())
			deleteDirectory(backup);		
	}
}
