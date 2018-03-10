package dbms;

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
		Table table = new Table();

		if (commandVector.elementAt(3).equals("ADD"))
		{
			if (db.getTable(commandVector.elementAt(2), table))
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
		Table table = new Table();

		if (db.getTable(commandVector.elementAt(3), table))
		{
			Map<String,String> columns;
			columns = table.getColumns();

			for (Map.Entry<String, String> entry : columns.entrySet())
			{
				message = message + entry.getKey() + " " + entry.getValue() + "| ";
			}
			if (!message.isEmpty())
				message = message.substring(0, message.length() - 2);
		}
		else
			message = "!Failed to query " + commandVector.elementAt(3) + " because it does not exist.";

		return message;
	}
	
	private String executeInsertIntoCommand(Vector<String> commandVector) {
		
		// Not implemented
		return "1 new record inserted.";
	}
	
	private String executeUpdateCommand(Vector<String> commandVector) {

		// Not implemented
		return "x record(s) modified";
	}
	
	private String executeDeleteCommand(Vector<String> commandVector) {

		// Not implemented
		return "x record(s) deleted";
	}
}
