package dbms;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javafx.util.Pair;

public class Executer {
	
	private Database db;
	private Parser parser;
	
	Executer() {
		this.parser = new Parser();
	}
	
	Executer(Database db) {
		this.parser = new Parser();
		this.db = db;
	}
	
	Database getDatabase() {
		return db;
	}
	
	String executeCommand(String command) {
		
		String result = null;
		String firstCommand = null;
		Vector<String> commandVector = new Vector<String>();

		// Send command to parser to check syntax
		result = parser.parseCommand(command);
		commandVector = parser.splitCommand(command);

		// If not valid syntax, set error message
		if (!result.equals("valid")) {
			result = "INVALID SQL SYNTAX: " + result;
			return result;
		}
		else
			firstCommand = commandVector.elementAt(0).toLowerCase();
			
		switch (firstCommand.toLowerCase()) {
        case "create":
        		result = executeCreateTableCommand(commandVector);
            break;
        case "drop":
        		result = executeDropTableCommand(commandVector);
        		break;
        case "alter":
        		result = executeAlterCommand(commandVector);
        		break;
        case "insert":
        		result = executeInsertIntoCommand(commandVector);
            break;
        case "select":
        		result = executeSelectCommand(commandVector);				
            break;
        case "update":
        		result = executeUpdateCommand(commandVector);
        		break;
        case "delete":
            result = executeDeleteCommand(commandVector);
            break;
        default: 
        		result = "INVALID SQL COMMAND: " + firstCommand;		
		}

		// Pass back String message
		return result;
	}

	private String executeCreateTableCommand(Vector<String> commandVector) {
		
		String message = null;

		// Create table without column info
		if (commandVector.size() < 3)
		{
			if (db.createTable(commandVector.elementAt(2)))
				message = "Table " + commandVector.elementAt(2) + " created.";
			else
				message = "!Failed to create table " + commandVector.elementAt(2) + " because it already exists.";
		}

		// If create command has column info
		else
		{
			Vector<Pair<String, String>> columnInfo;

			columnInfo = parser.parseColumnInfo(commandVector);

			if (db.createTable(commandVector.elementAt(2), columnInfo))
				message = "Table " + commandVector.elementAt(2) + " created.";
			else
				message = "!Failed to create table " + commandVector.elementAt(2) + " because it already exists.";
		}

		return message;	
	}

	private String executeDropTableCommand(Vector<String> commandVector)	{
		
		String message = null;
		
		if (db.dropTable(commandVector.elementAt(2)))
			message = "Table " + commandVector.elementAt(2) + " deleted.";
		else
			message = "!Failed to delete " + commandVector.elementAt(2) + " because it does not exist.";

		return message;	
	}

	private String executeAlterCommand(Vector<String> commandVector)	{
		
		String message = null;
		Table table;

		if (commandVector.elementAt(3).equals("ADD"))
		{
			table = db.getTable(commandVector.elementAt(2));
			
			if (table != null)
			{
				table.createColumn(commandVector.elementAt(4), commandVector.elementAt(5));
				message = "Table " + table.getTableName() + " modified.";
			}
			else
				message = "Table " + commandVector.elementAt(2) + " was not found.";
		}
		return message;
	}

	private String executeSelectCommand(Vector<String> commandVector) {
	
		String message = new String();
		Table table = db.getTable(commandVector.elementAt(3));

		if (table != null && commandVector.size() == 4) {
			Map<String,String> columns;
			columns = table.getColumns();

			for (String key : columns.keySet()) {
				message = message + key + " " + columns.get(key) + "|";
			}
			if (!message.isEmpty())
				message = message.substring(0, message.length() - 1);
			
			Iterator<Row> it = table.getTableData();
			while (it.hasNext()) {
				message += "\n";
				for (String key : columns.keySet())
					message += it.next().getData(key) + "|";
				message = message.substring(0, message.length() - 1);
			}
			
		}
		else if (commandVector.size() > 4) {
			message = "Non SELECT * FROM select statements have not been implemented.";
		}
		else
			message = "!Failed to query " + commandVector.elementAt(3) + " because it does not exist.";

		return message;
	}
	
	private String executeInsertIntoCommand(Vector<String> commandVector) {
		
		String[] commandArray = commandVector.toArray(new String[commandVector.size()]);
		Table table = db.getTable(commandArray[2]);
		Map<String, String> columns = table.getColumns();
		int commandIndex = 4;
		Row row = new Row();
		
		for (String key : columns.keySet()) {
			row.addData(key, columns.get(key), commandArray[commandIndex]);
			table.addRow(row);
			commandIndex++;
		}
		
		return "1 new record inserted.";
	}
	
	private String executeUpdateCommand(Vector<String> commandVector) {

		String[] commandArray = commandVector.toArray(new String[commandVector.size()]);
		Table table = db.getTable(commandArray[1]);
		int updateCount = 0;

		if (table != null) {
			Map<String,String> columns;
			columns = table.getColumns();

			// Look for the column that we are setting
			if (columns.containsKey(commandArray[3])) {
				
				// Look for the column of the WHERE statement
				if (columns.containsKey(commandArray[7])) {
					
					// Iterate through rows and update values
					Iterator<Row> it = table.getTableData();
					while (it.hasNext()) {
						// If a match for the where clause
						if (it.next().getData(commandArray[7]).equals(commandArray[9])) {
							// Check to make sure the column exists that we are updating
							if (it.next().getData(commandArray[3]) != null) {
								it.next().data.put(commandArray[3], commandArray[5]);
								updateCount++;							 	
							}
						}
					}		
				}
			}			
		}
		else
			return "Table " + commandArray[1] + " does not exist.";
		
		if (updateCount == 1)
			return "1 record modified.";
		if (updateCount > 1)
			return updateCount + " records modified";
					
		return "0 records modified.";
	}
	
	private String executeDeleteCommand(Vector<String> commandVector) {
					
		return "Record deletion not implemented.";
	}
	
}
