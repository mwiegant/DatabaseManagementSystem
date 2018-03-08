package dbms;

import java.util.Arrays;
import java.util.Vector;
import javafx.util.Pair;

public class Parser {
	
	Parser()
	{
		
	}

	String parseCommand(String command)
	{
		String result;
		Vector<String> commandVector;

		// Break up command into a array of Strings
		commandVector = splitCommand(command);

		// Check for valid SQL syntax ----- For future projects
		if (commandVector.size() == 0)
			result = "Command was empty";
		else if (commandVector.elementAt(0) == "CREATE")
			result = validateCreateStatement(commandVector);
		else if (commandVector.elementAt(0) == "DROP")
			result = validateDropStatement(commandVector);
		else if (commandVector.elementAt(0) == "ALTER")
			result = validateAlterStatement(commandVector);
		else if (commandVector.elementAt(0) == "SELECT")
			result = validateSelectStatement(commandVector);
		else
			result = commandVector.elementAt(0) + " is not a valid SQL command\n\n";

		return result;
	}

	Vector<String> splitCommand(String command)
	{
		// Remove semicolon at end
		command = command.substring(0, command.length() - 1);
		String[] commandVector = command.split(" ");
		
		// If a create statement with column info, format the column info
		if (commandVector.length > 3 && commandVector[0] == "CREATE")
		{
			// Remove the left parentheses of column info
			commandVector[3] = commandVector[3].substring(1);
			// Remove the last parentheses of column info
			String tempWithComma = commandVector[commandVector.length - 1];
			commandVector[commandVector.length - 1] = tempWithComma.substring(0, tempWithComma.length() - 1);

			// Remove any commas that might be in strings
			for (String s : commandVector)
			{
				s.replaceAll(",", "");
			}
		}

		return new Vector<String>(Arrays.asList(commandVector));
	}

	Vector<Pair<String,String>> parseColumnInfo(Vector<String> commandVector)
	{
		Vector<Pair<String,String>> columnInfo = new Vector<Pair<String, String>>();

		for (int i = 3; i < commandVector.size(); i+=2)
		{
			columnInfo.add(new Pair<String, String>(commandVector.elementAt(i), commandVector.elementAt(i + 1)));
		}
		return columnInfo;
	}


	String validateCreateStatement(Vector<String> command)
	{
		return "valid";
	}

	String validateDropStatement(Vector<String> command)
	{
		return "valid";
	}

	String validateAlterStatement(Vector<String> command)
	{
		return "valid";
	}

	String validateSelectStatement(Vector<String> command)
	{
		return "valid";
	}
	
}
