package dbms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		List<JoinCriteria> joinCriteria = null;
		List<Row> matchingRows = new ArrayList<Row>();
		List<Row> otherRows = new ArrayList<Row>();

		Table table = null;
		boolean isJoinQuery = false;

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
		
		
		// parse join or selection criteria
		isJoinQuery = isJoinQuery(commandVector);
		
		if (isJoinQuery)
			joinCriteria = parseJoinSelectionCriteria(commandVector);
		else
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
		
		
		if (joinCriteria != null && !joinCriteria.isEmpty()) {
			
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
        		if (isJoinQuery) {
        			result = executeJoinSelectCommand(joinCriteria, commandVector);        		
        		} else
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

		// SELECT * FROM table_name
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
		// If more complicated than select * from table
		else if (table != null && commandVector.size() > 4) {
			// Get the columns for the table
			Map<String,String> columns;
			columns = table.getColumns();

			// Add headers of table to message
			for (int headerIndex = 1; !commandVector.elementAt(headerIndex).equals("from"); headerIndex++) {
				message = message + commandVector.elementAt(headerIndex) + " " + columns.get(commandVector.elementAt(headerIndex)) + "|";
			}
			if (!message.isEmpty())
				message = message.substring(0, message.length() - 1);
			
			// Loop through rows
			for (int i = 0; i < matchingRows.size()/3; i++) { 
				message += "\n";
				// Loop through the data of the row and add it to message
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
			commandIndex++;
		}
		
		table.addRow(row);		
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
	
	private String executeJoinSelectCommand(List<JoinCriteria> joinCriteria, Vector<String> commandVector) {
		// I am assuming only one join criteria, although this code could be expanded to support multiple join criteria
		String joinType = joinCriteria.get(0).joinType; 
		String leftTableName = joinCriteria.get(0).leftTableName;
		String leftColumnName = joinCriteria.get(0).leftColumnName;
		String rightTableName = joinCriteria.get(0).rightTableName;
		String rightColumnName = joinCriteria.get(0).rightColumnName;
		Table leftTable = db.getTable(leftTableName);
		Table rightTable = db.getTable(rightTableName);
		
		switch (joinType) {
		case "inner_join":
			executeInnerJoin(leftTable, rightTable, leftColumnName, rightColumnName);
			break;
		case "left_outer_join":
			executeLeftOuterJoin(leftTable, rightTable, leftColumnName, rightColumnName);
			break;	
		default:
			return "Invalid joinType: " + joinType;
		}
		
		return "";
	}

	
	private String executeInnerJoin(Table leftTable, Table rightTable, String leftColumn, String rightColumn) {
		String columnType = leftTable.getColumns().get(leftColumn); // column types should be the same
		Set<String> leftColumns = leftTable.getColumns().keySet();
		Set<String> rightColumns = rightTable.getColumns().keySet();		

		Iterator<Row> leftRowIt = leftTable.getTableData();
		Iterator<Row> rightRowIt = null;
		boolean firstOutput = true;
		Row leftRow;
		Row rightRow;
		
		// for each row in leftTable
		while (leftRowIt.hasNext()) {
			leftRow = leftRowIt.next();			
			rightRowIt = rightTable.getTableData();
			
			while (rightRowIt.hasNext()) {
				rightRow = rightRowIt.next();
				
				if (firstOutput) {
					printColumns(leftColumns, rightColumns);
					firstOutput = false;
				}
				
				// inner join, only print the data if both sides of the join have the same value
				if (areEqual(columnType, leftRow.getData(leftColumn), rightRow.getData(rightColumn))) {	
					printJoinedRow(leftColumns, rightColumns, leftRow, rightRow);
				}
			}
		}		
		
		return "";
	}
	
	
	private String executeLeftOuterJoin(Table leftTable, Table rightTable, String leftColumn, String rightColumn) {
		String columnType = leftTable.getColumns().get(leftColumn); // column types should be the same
		Set<String> leftColumns = leftTable.getColumns().keySet();
		Set<String> rightColumns = rightTable.getColumns().keySet();		

		Iterator<Row> leftRowIt = leftTable.getTableData();
		Iterator<Row> rightRowIt = null;
		boolean firstOutput = true;
		boolean matched;
		Row leftRow;
		Row rightRow;
		
		// for each row in leftTable
		while (leftRowIt.hasNext()) {
			leftRow = leftRowIt.next();			
			rightRowIt = rightTable.getTableData();
			matched = false;
			
			// first output, print columns
			if (firstOutput) {
				printColumns(leftColumns, rightColumns);
				firstOutput = false;
			}
			
			while (rightRowIt.hasNext()) {
				rightRow = rightRowIt.next();
								
				// print data from both sides of the join if the data is equal
				if (areEqual(columnType, leftRow.getData(leftColumn), rightRow.getData(rightColumn))) {
					printJoinedRow(leftColumns, rightColumns, leftRow, rightRow);
					matched = true;
				}
			}
			
			// if the data from the left side of the join never matched with data from the right side,
			// just print the data from the left side by itself
			if (matched == false)
				printJoinedRow(leftColumns, rightColumns, leftRow, null);
		}		
		
		return "";
	}
	
	/*
	 * a "one-size-fits-all" equality checking function, for checking values for table joins
	 * 
	 * @param dataType - the type of this data (int, float, char(20), etc)
	 */
	private boolean areEqual(String dataType, Object leftObject, Object rightObject) {		
		// null pointer exceptions may be thrown. In that case, just return false
		try {
		
			switch (dataType) {
			case "int":
				return Integer.valueOf((String) leftObject) == Integer.valueOf((String) rightObject);
	
			case "float":
				return Float.valueOf((String) leftObject) == Float.valueOf((String) rightObject); 
				
			case "varchar(20)":
			case "char(20)":
				return ((String) leftObject).equals((String) rightObject);
				
			default:
				System.out.println("!Error - invalid column type: " + dataType);
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/*
	 * For table joins, prints the columns. Meant to be called prior to printJoinedRow().
	 */
	private void printColumns(Set<String> leftColumns, Set<String> rightColumns) {
		boolean firstColumn = true;
		
		for (String column : leftColumns) {
			if (firstColumn) {
				System.out.print(column);
				firstColumn = false;
			}
			else
				System.out.print("|" + column);
		}
		
		for (String column : rightColumns)
			System.out.print("|" + column);
		
		System.out.print("\n");
	}
	
	/*
	 * Prints the data from two rows, in an inner join
	 */
	private void printJoinedRow(Set<String> leftColumns, Set<String> rightColumns, Row _leftRow, Row _rightRow) {
		boolean firstColumn = true;
		Row leftRow, rightRow;
		
		// if either row is null, initialize it but expect that data from that row will be null
		if (_leftRow == null)
			leftRow = new Row();
		else
			leftRow = _leftRow;
		
		if (_rightRow == null)
			rightRow = new Row();
		else
			rightRow = _rightRow;
		
		
		// print data from left row
		for (String column : leftColumns) {
			if (firstColumn) {
				firstColumn = false;
				
				if (leftRow.getData(column) == null)
					System.out.print("");
				else
					System.out.print(leftRow.getData(column));
			}
			else {
				if (leftRow.getData(column) == null)
					System.out.print("|");
				else {
					System.out.print("|");
					System.out.print(leftRow.getData(column));
				}
			}
				
		}
		
		// print data from right row
		for (String column : rightColumns) {
			if (rightRow.getData(column) == null)
				System.out.print("|");
			else {
				System.out.print("|");
				System.out.print(rightRow.getData(column));
			}
		}		
		
		System.out.print("\n");
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
	
	
	private List<JoinCriteria> parseJoinSelectionCriteria(Vector<String> commandVector) {
		Iterator<String> tokens = commandVector.iterator();
		List<String> tableNameTokens = new ArrayList<String>();
		List<JoinCriteria> allJoinCriteria = new ArrayList<JoinCriteria>();
		Map<String, String> tableNames;
		JoinCriteria joinCriteria = new JoinCriteria();
		String token;
		String[] splitTokens;
		int index = 1;
		
		// iterate until we hit the from statement
		while (tokens.hasNext()) {
			if (tokens.next().equals("from"))
				break;
		}
		
		// parse table name aliases and join type
		tableNames = parseTableNamesAndJoinType(tokens, tableNameTokens);
		
		// parse table selection criteria
		while (tokens.hasNext()) {
			token = tokens.next();
			
			switch(index) {
			case 1:
				joinCriteria = new JoinCriteria();
				splitTokens = token.split("\\.");
				
				joinCriteria.leftTableName = tableNames.get(splitTokens[0]);
				joinCriteria.leftColumnName = splitTokens[1];
				break;
			case 2:
				// do nothing, we assume equality for table joins
				break;
			case 3:
				splitTokens = token.split("\\.");
				
				joinCriteria.rightTableName = tableNames.get(splitTokens[0]);
				joinCriteria.rightColumnName = splitTokens[1];
				joinCriteria.joinType = tableNames.get("joinType");
				allJoinCriteria.add(joinCriteria);
				break;
			}
			
			// this is my way of keeping track of where I am in "condition = condition" (3 seperate tokens)
			index = (index == 3) ? 1 : index + 1;
		}

		return allJoinCriteria;
	}
	
	private Map<String, String> parseTableNamesAndJoinType(Iterator<String> iter, List<String> tableNameTokens) {
		Map<String, String> map = new HashMap<String, String>();
		String tableNameOne, tableAliasOne, tableNameTwo, tableAliasTwo, token;
		String joinType = "";
		String defaultJoinType = "inner_join";
		int index;
		
		// grab all tokens between FROM and WHERE / ON
		while(iter.hasNext()) {
			token = iter.next();
			
			if (token.equals("where") || token.equals("on"))
				break;
			else
				tableNameTokens.add(token);
		}
				
		// case where only the table names are present, with an implied inner join
		if (tableNameTokens.size() == 4) {
			tableNameOne = tableNameTokens.get(0);
			tableAliasOne = tableNameTokens.get(1);
			tableNameTwo = tableNameTokens.get(2);
			tableAliasTwo = tableNameTokens.get(3);
			joinType = defaultJoinType;
		}
		// case where the join type is several words that exist between the two table names
		else {
			tableNameOne = tableNameTokens.get(0);
			tableAliasOne = tableNameTokens.get(1);
			
			// increment through list until "join" is reached			
			index = 2;
			while (!tableNameTokens.get(index).equals("join")) {
				if (joinType.length() == 0)
					joinType = tableNameTokens.get(index);
				else
					joinType += "_" + tableNameTokens.get(index);
				
				index++;
			}
			
			// add "join" to the joinType
			joinType += "_" + tableNameTokens.get(index);
			index++;
			
			tableNameTwo = tableNameTokens.get(index);
			index++;
			tableAliasTwo = tableNameTokens.get(index);
		}
		
		map.put(tableAliasOne, tableNameOne);
		map.put(tableAliasTwo, tableNameTwo);
		map.put("joinType", joinType);
				
		return map;
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
	
	private boolean isJoinQuery(Vector<String> commandVector) {
		Iterator<String> tokens = commandVector.iterator();
		
		while(tokens.hasNext())
			if (tokens.next().contains("."))
				return true;	
		return false;
	}
	
	
}
