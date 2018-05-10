package dbms;

import java.util.Arrays;
import java.util.Vector;
import javafx.util.Pair;

public class Parser {
	
	Parser() {
		
	}

	String parseCommand(String command) {
		
		String result = null;
		String firstCommand = null;
		Vector<String> commandVector;

		// 
		
		
		// Break up command into a array of Strings
		commandVector = splitCommand(command);
		
		// Check to make sure the command wasn't empty, else get the first command
		if (commandVector.size() <= 0) {
			result = "Command was empty";
			return result;
		}
		else 
			firstCommand = commandVector.elementAt(0);
		
		switch (firstCommand.toLowerCase()) {
        case "create":
        		result = validateCreateStatement(commandVector);
            break;
        case "drop":
        		result = validateDropStatement(commandVector);
        		break;
        case "alter":
        		result = validateAlterStatement(commandVector);
        		break;
        case "insert":
        		result = validateInsertStatement(commandVector);
            break;
        case "select":
        		result = validateSelectStatement(commandVector);
            break;
        case "update":
        		result = validateUpdateStatement(commandVector);
        		break;
        case "delete":
            result = validateDeleteStatement(commandVector);
            break;
        case "begin":
        		result = (commandVector.get(1).replace(";", "")).equals("transaction") ? "valid" : "invalid";
        		break;
        case "commit":
        case "commit;":
        		result = "valid";
    			break;        
        default: 
        		result = String.join(" ", commandVector.toArray(new String[commandVector.size()]));   		
		}
		
		return result;
	}

	Vector<String> splitCommand(String command) {
		command = command.replace("(", " (").replace(",", ", ");
		command = command.replace("  ", " ").replace(";", "");
		
		String[] commandArray = command.split(" ");
		String[] result = null;
		String firstCommand = null;
		
		// Make sure the command wasn't empty, return empty vector if it was and parseCommand will catch it
		if (commandArray.length > 0)
			firstCommand = commandArray[0];
		else
			return new Vector<String>(Arrays.asList(commandArray));
		
		switch (firstCommand.toLowerCase()) {
	        case "create":
	        		result = splitTableModifierCommand(command);
	            break;
	        case "drop":
	        		result = splitTableModifierCommand(command);
	        		break;
	        case "alter":
	        		result = splitTableModifierCommand(command);
	        		break;
	        case "insert":
	        		result = splitInsertCommand(command);
	            break;
	        case "select":
	            result = splitSelectCommand(command);
	            break;
	        case "update":
	        		result = splitUpdateCommand(command);
	        		break;
	        case "delete":
	            result = splitDeleteCommand(command);
	            break;
	        default:
	            result = commandArray;
		}		
		return new Vector<String>(Arrays.asList(result));
	}
	
	private String[] splitTableModifierCommand(String command) {
		
		command = removeSemicolonAtEnd(command);
		
		// Split by space
		String[] commandArray = command.split(" ");
		
		// If a create statement with column info, format the column info
		if (commandArray.length > 3 && commandArray[0].toLowerCase().equals("create")) {
			// Remove the left parentheses of column info
			commandArray[3] = commandArray[3].substring(1);
			
			// Remove the last parentheses of column info
			String tempWithComma = commandArray[commandArray.length - 1];
			commandArray[commandArray.length - 1] = tempWithComma.substring(0, tempWithComma.length() - 1);

			// Remove any commas that might be in strings
			for (int i = 0; i < commandArray.length; i++) {
				commandArray[i] = commandArray[i].replaceAll(",", "");
			}
		}
		return commandArray;
	}
	
	private String[] splitSelectCommand(String command) {
		
		command = removeSemicolonAtEnd(command);
		
		String[] commandArray = command.split(" ");
		
		// Remove any commas from the command vector components
		for (int i = 0; i < commandArray.length; i++) {
			commandArray[i] = commandArray[i].replaceAll(",", "");
			commandArray[i] = commandArray[i].replaceAll("'", "");
			commandArray[i] = commandArray[i].replaceAll("\"", "");
		}
		
		return commandArray;
	}
	
	private String[] splitInsertCommand(String command) {
		
		Vector<String> commandVector = new Vector<String>();
		command = removeSemicolonAtEnd(command);
		
		// Split the command by operation and data | operation => insert into tableName  data => (id, <value>, <value>)
		String[] commandPair = command.split("\\(");
		
		// HANDLE OPERATION STRING
		// Split the operation words
		String[] operationComponents = commandPair[0].split(" ");
		
		// Add the operation components to the command vector
		for (String s : operationComponents) {
			if (!s.equals(" "))
				commandVector.add(s);
		}
		
		// HANDLE DATA STRING
		// Remove closing parenthese from data string
		String dataString = commandPair[1];
		dataString = dataString.substring(0, dataString.length() - 1);
		
		// Remove commas and tabs from data string
		dataString = dataString.replaceAll(",", "");
		// Replace tabs with spaces
		dataString = dataString.replaceAll("\t", " ");
		
		// Split the data string by spaces
		String[] dataComponents = dataString.split(" ");
		
		// Trim away spaces and tabs from data components
		for (int i = 0; i < dataComponents.length; i++) {
			dataComponents[i] = dataComponents[i].trim();
			dataComponents[i] = dataComponents[i].replaceAll("'", "");
			dataComponents[i] = dataComponents[i].replaceAll("\"", "");
		}
		
		// Add the data components to command vector
		for (String s : dataComponents) {
			if (!s.equals("") && !s.equals("\t") && !s.equals(" "))
				commandVector.add(s);
		}
		
		return commandVector.toArray(new String[commandVector.size()]);
	}
	
	private String[] splitUpdateCommand(String command) {
		
		command = removeSemicolonAtEnd(command);
		String[] commandArray = command.split(" ");
		
		for (int i = 0; i < commandArray.length; i++) {
			commandArray[i] = commandArray[i].replaceAll("'", "");
			commandArray[i] = commandArray[i].replaceAll("\"", "");
		}
		
		return commandArray;
	}
	
	private String[] splitDeleteCommand(String command) {
		
		command = removeSemicolonAtEnd(command);
		String[] commandArray = command.split(" ");
		
		for (int i = 0; i < commandArray.length; i++) {
			commandArray[i] = commandArray[i].replaceAll("'", "");
			commandArray[i] = commandArray[i].replaceAll("\"", "");
		}
		
		return commandArray;
	}
	
	Vector<Pair<String,String>> parseColumnInfo(Vector<String> commandVector) {
		
		Vector<Pair<String,String>> columnInfo = new Vector<Pair<String, String>>();

		for (int i = 3; i < commandVector.size(); i+=2)
		{
			columnInfo.add(new Pair<String, String>(commandVector.elementAt(i), commandVector.elementAt(i + 1)));
		}
		return columnInfo;
	}


	String validateCreateStatement(Vector<String> commandVector) {
		
		if (commandVector.elementAt(0).toLowerCase().equals("create") && 
				commandVector.elementAt(1).toLowerCase().equals("table") &&
				!commandVector.elementAt(2).isEmpty()) {
			
			return "valid";
		}
		
		return "invalid";
	}

	String validateDropStatement(Vector<String> commandVector) {
		
		if (commandVector.elementAt(0).toLowerCase().equals("drop") && 
				commandVector.elementAt(1).toLowerCase().equals("table") &&
				!commandVector.elementAt(2).isEmpty()) {
			
			return "valid";
		}
		
		return "invalid";
	}

	String validateAlterStatement(Vector<String> commandVector) {
		
		if (commandVector.elementAt(0).toLowerCase().equals("alter") && 
				commandVector.elementAt(1).toLowerCase().equals("table") &&
				!commandVector.elementAt(2).isEmpty()) {
			
			return "valid";
		}
		// not fully implemented
		return "invalid";
	}

	String validateSelectStatement(Vector<String> commandVector) {
		// not implemented
		return "valid";
	}
	
	String validateInsertStatement(Vector<String> commandVector) {
		// not implemented
		return "valid";
	}
	
	String validateUpdateStatement(Vector<String> commandVector) {
		// not implemented
		return "valid";
	}
	
	String validateDeleteStatement(Vector<String> commandVector) {
		// not implemented
		return "valid";
	}
	
	String removeSemicolonAtEnd(String command) {
		
		// Remove semicolon at end if there is one
		if (command.substring(command.length() - 1).equals(";")) {
			command = command.substring(0, command.length() - 1);
		}
		
		return command;
	}
	
}
