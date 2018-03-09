package dbms;

import java.util.Map;
import java.util.Vector;
import javafx.util.Pair;

public class Executer {
	
	private Database db;
	private Parser parser;
	
	Executer()
	{
		this.parser = new Parser();
	}
	
	Executer(Database db)
	{
		this.parser = new Parser();
		this.db = db;
	}
	
	Database getDatabase()
	{
		return db;
	}
	
	String executeCommand(String command)
	{
		String result;
		Vector<String> commandVector;

		// Send command to parser to check syntax
		result = parser.parseCommand(command);
		commandVector = parser.splitCommand(command);

		if (!result.equals("valid"))
			result = "INVALID SQL SYNTAX: " + result;

		// Otherwise execute command
		else if (commandVector.elementAt(0).equals("CREATE") && commandVector.elementAt(1).equals("TABLE"))
			result = executeCreateTableCommand(commandVector);
		else if (commandVector.elementAt(0).equals("DROP") && commandVector.elementAt(1).equals("TABLE"))
			result = executeDropTableCommand(commandVector);
		else if (commandVector.elementAt(0).equals("ALTER"))
			result = executeAlterCommand(commandVector);
		else if (commandVector.elementAt(0).equals("SELECT"))
			result = executeSelectCommand(commandVector);
		else
			result = "INVALID SQL COMMAND";

		// Pass back String message
		return result;
	}
	
	String executeCreateTableCommand(Vector<String> commandVector)
	{
		String message;

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

	String executeDropTableCommand(Vector<String> commandVector)
	{
		String message;
		
		if (db.dropTable(commandVector.elementAt(2)))
			message = "Table " + commandVector.elementAt(2) + " deleted.";
		else
			message = "!Failed to delete " + commandVector.elementAt(2) + " because it does not exist.";

		return message;	
	}

	String executeAlterCommand(Vector<String> commandVector)
	{
		String message = new String();

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

	String executeSelectCommand(Vector<String> commandVector)
	{
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
}
