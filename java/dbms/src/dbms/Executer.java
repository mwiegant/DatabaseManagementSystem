package dbms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
		List<Criteria> criteria = null;
		List<Row> matchingRows = new ArrayList<Row>();
		List<Row> otherRows = new ArrayList<Row>();
		Table table = null;

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
			
		// Get selection criteria
		criteria = parseSelectionCriteria(commandVector);
		
		if (criteria != null && !criteria.isEmpty()) {
			String tableName = extractTableName(commandVector);
			if (tableName != null) {
				table = db.getTable(tableName);
				// Loop through rows and separate them into criteria matches non matches
				Iterator<Row> it = table.getTableData();
				while (it.hasNext()) {
					Row currentRow = it.next();
					if (this.matchesCriteria(currentRow, criteria)) 
						matchingRows.add(currentRow);
					else
						otherRows.add(currentRow);
				}
			}
		}
		
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
        		result = executeSelectCommand(matchingRows, otherRows, commandVector);				
            break;
        case "update":
        		result = executeUpdateCommand(commandVector);
        		break;
        case "delete":
            result = executeDeleteCommand(matchingRows, otherRows, commandVector);
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

	private String executeSelectCommand(List<Row> matchingRows, List<Row> otherRows, Vector<String> commandVector) {
	
		String message = new String();
		Table table = db.getTable(extractTableName(commandVector));

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
		else if (table != null && commandVector.size() > 4) {
			Map<String,String> columns;
			columns = table.getColumns();

			for (int headerIndex = 1; !commandVector.elementAt(headerIndex).equals("from"); headerIndex++) {
				message = message + commandVector.elementAt(headerIndex) + " " + columns.get(commandVector.elementAt(headerIndex)) + "|";
			}
			if (!message.isEmpty())
				message = message.substring(0, message.length() - 1);
			
			for (int i = 0; i < matchingRows.size()/3; i++) { 

				message += "\n";
				for (int headerIndex = 1; !commandVector.elementAt(headerIndex).equals("from"); headerIndex++)
					message += matchingRows.get(i).getData(commandVector.elementAt(headerIndex)) + "|";
				message = message.substring(0, message.length() - 1);
			}
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
						switch (commandArray[8]) { 
						case "=" : {
							if (it.next().getData(commandArray[7]).equals(commandArray[9])) {
								// Check to make sure the column exists that we are updating
								if (it.next().getData(commandArray[3]) != null) {
									it.next().data.put(commandArray[3], commandArray[5]);
									updateCount++;							 	
								}
							}
						}
						break;
						
						case ">" : {
							// Convert to int and compare
							if (it.next().getData(commandArray[7]).equals(commandArray[9])) {
								// Check to make sure the column exists that we are updating
								if (it.next().getData(commandArray[3]) != null) {
									it.next().data.put(commandArray[3], commandArray[5]);
									updateCount++;							 	
								}
							}
						}
						break;
						
						case "<" : {
							// Convert to int and compare
							if (it.next().getData(commandArray[7]).equals(commandArray[9])) {
								// Check to make sure the column exists that we are updating
								if (it.next().getData(commandArray[3]) != null) {
									it.next().data.put(commandArray[3], commandArray[5]);
									updateCount++;							 	
								}
							}
						}
						default:
							
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
	
	private String executeDeleteCommand(List<Row> matchingRows, List<Row> otherRows, Vector<String> commandVector) {
		
		Table table = db.getTable(extractTableName(commandVector));
		table.setTableData(otherRows);
		
		// Have to divide by three because for some reason it adds a bunch of null values
		if (matchingRows.size()/3 == 1)
			return "1 record deleted.";
		
		return matchingRows.size()/3 + " records deleted.";
	}
	
	private List<Criteria> parseSelectionCriteria(Vector<String> commandVector) {
		
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		String[] commandArray = commandVector.toArray(new String[commandVector.size()]);
		
		if (commandArray.length > 5) {
			for (int i = 0; i < commandArray.length; i++) {
				if (commandArray[i].equals("where")) {
					criteriaList.add(new Criteria(commandArray[i+1], commandArray[i+3], commandArray[i+2]));
					if (i + 5 <= commandArray.length)
						i += 4;
					while (commandArray[i].equals("and")) {
						criteriaList.add(new Criteria(commandArray[i+1], commandArray[i+3], commandArray[i+2]));
						if (i + 5 <= commandArray.length)
							i += 4;
					}
				}
			}
		}
		
		return criteriaList;
	}
	
	private boolean matchesCriteria(Row row, List<Criteria> criteriaList) {
		
		for (Criteria criteria : criteriaList) {
			switch (criteria.operator) {
			case "=" : 
				return row.getData(criteria.colName).equals(criteria.value);
			
			case "!=" : 
				return !row.getData(criteria.colName).toString().equals(criteria.value);
				
			case ">" : 
				return (Float.parseFloat(row.getData(criteria.colName).toString()) > Float.parseFloat(criteria.value));
				
			case "<" : 
				return (Float.parseFloat(row.getData(criteria.colName).toString()) < Float.parseFloat(criteria.value));
				
			case ">=" : 
				return (Float.parseFloat(row.getData(criteria.colName).toString()) >= Float.parseFloat(criteria.value));
				
			case "<=" : 
				return (Float.parseFloat(row.getData(criteria.colName).toString()) <= Float.parseFloat(criteria.value));	
			
			default :
				break;
			}
			
		}
		return false;
	}
	
	private String extractTableName(Vector<String> commandVector) {
		String[] commandArray = commandVector.toArray(new String[commandVector.size()]);
		
		for (int i = 0; i < commandArray.length; i++) {
			if (commandArray[i].toLowerCase().equals("from") || commandArray[i].toLowerCase().equals("update"))
				return commandArray[i+1];
		}
		return null;
	}
	
}
