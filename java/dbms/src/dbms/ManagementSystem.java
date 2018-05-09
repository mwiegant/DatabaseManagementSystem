package dbms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManagementSystem {
	
	private static final String DIR = "../db";
	private String workingDirectory = null;
	private String architecture = null;
  	DatabasePersister databasePersister = new DatabasePersister();
  	Database database = null;
    Executer executer = new Executer();
    Parser parser = new Parser();    
    List<String> databaseNames = new ArrayList<String>();
    boolean exitProgram = false;
    
	ManagementSystem(String wdir, String arch) {
		this.workingDirectory = wdir;
		this.architecture = arch;
		
		// attempt to cache database names, and kill the program immediately if this fails
		if (!cacheDatabaseNames())
			exitProgram = true;
	}

	// reads from an sql file, running one command at a time, until the end of the file
	public void RunInScriptMode(String sqlFilename)	{
		List<String> commands = new ArrayList<String>();

		// exit the program if some pre-condition has caused a faulty state
		if(exitProgram) {
			System.out.println("Terminating program due to an error, likely from failure to cache database names.");
			return;
		}
		
		// exit program if it fails to load the file
		if (!getCommandsFromFile(buildSqlFilenamePath(sqlFilename), commands))
		{
			System.out.println(String.format("Error - file does not exist: %1$s.", sqlFilename));
			return;
		}
		else {
			// run one command at a time from the file
			for (String command : commands)
				processCommand(command);
		}
	}


	// runs the program indefinitely, running a command at a time
	public void RunInCommandLineMode() {
		Scanner reader = new Scanner(System.in);
		String command = "";
		String userInput;
		int semicolonIndex;

		// exit the program if some pre-condition has caused a faulty state
		if(exitProgram) {
			System.out.println("Terminating program due to an error, likely from failure to cache database names.");
			reader.close();
			return;
		}
		
		// continually loop while user does not enter "exit" or ".exit"
		do {		
			
			System.out.print(" > ");
			userInput = reader.nextLine();
			
			// check for a .EXIT command
			if (userInput.toLowerCase().equals(".exit")) {
				processCommand(userInput);
			}
			
			// check for a semicolon, and truncate the command at that point if there is one
			semicolonIndex = userInput.indexOf(";");
			
			// there is a semicolon, so build the rest of the command and process it
			if (semicolonIndex != -1) {
				command += userInput.substring(0, semicolonIndex);
				processCommand(command + ";");
				command = "";
				
			}			
			// else there is no semicolon, just append the user input to the working command
			else {
				command += userInput;
			}
			
		} while (!exitProgram);
		
		reader.close();
	}


	/*
	 *	Attempt to load in a new database with the user's specified database name.
	 *	If the specified database exists but a database is already loaded, saves
	 *	the existing database to file before overriding it with the new database. 
	 */
	private boolean loadDatabase(String dbName)
	{
		Database newDb;

		newDb = databasePersister.loadDatabase(buildCompleteDatabasePath(dbName), dbName);

		// check if the database actually exists, and return error if it does not
		if (newDb == null)
			return false;

		// check if a database object was already loaded, and if so save it now
		if (database != null)
			 databasePersister.saveDatabase(buildCompleteDatabasePath(dbName), database);

		database = newDb;
		executer = new Executer(database, buildCompleteDatabasePath(dbName));		
		return true;
	}


	// process one SQL command
	private void processCommand(String command)
	{
		String lowercaseCommand = command.toLowerCase();	

		// check for an exit command
		if (lowercaseCommand.equals(".exit") || lowercaseCommand.equals("exit")) {

			// check if a database object was already loaded, and if so save it now
			if (database != null)
				 databasePersister.saveDatabase(buildCompleteDatabasePath(database.getDatabaseName()), database);
			
			System.out.println("All done.");
			exitProgram = true;
			return;
		}

		// check for a 'use database' command
		else if (lowercaseCommand.startsWith("use ")) {
			String[] tokens = lowercaseCommand.replace(';', ' ').split(" ");
			// load a new database, creating it or fetching it as necessary, and save the existing database
			if (loadDatabase(tokens[1]))
				System.out.println(String.format("Using database %1$s.", tokens[1]));
			else
				System.out.println(String.format("!Failed to load '%1$s' database.", tokens[1]));				
		}

		// check for a 'create database' or 'drop database' command
		else if (lowercaseCommand.startsWith("create database") || lowercaseCommand.startsWith("drop database")) {
			System.out.println(processDatabaseCommand(lowercaseCommand));
		}

		// otherwise, send command to Executer and let it handle the command
		else {
			if (executer == null)
				System.out.println("!Failed - must specify a database before accepting any commands.");
			// else, pass the original command to the executer
			else			
				System.out.println(executer.executeCommand(command));
		}
	}


	// processes commands to create and drop databases
	private String processDatabaseCommand(String lowercaseCommand)
	{
		String dbName;
		List<String> commandTokens = new ArrayList<String>();
		
		for (String token : lowercaseCommand.split(" "))
			commandTokens.add(token);
		
		// check for invalid command syntax
		if(commandTokens.size() != 3)
			return "!Failed to process command, invalid syntax.";

		dbName = commandTokens.get(2).replace(";", "");

		// try to create database
		if (commandTokens.get(0).equals("create") && commandTokens.get(1).equals("database"))
		{
			// see if database already exists
			if (databaseNames.contains(dbName))
				return "!Failed to create database " + dbName + " because it already exists.";

			// otherwise, create the database
			databaseNames.add(dbName);
			databasePersister.initializeDatabase(buildDatabasePath(dbName));
			return "Database " + dbName + " created.";
		}

		// try to drop database
		else if (commandTokens.get(0).equals("drop") && commandTokens.get(1).equals("database"))
		{
			// see if database does not exist
			if (!databaseNames.contains(dbName))
				return "!Failed to delete " + dbName + " because it does not exist.";

			databaseNames.remove(dbName);
			databasePersister.dropDatabase(buildDatabasePath(dbName));
			return "Database " + dbName + " deleted.";
		}

		else
			return "!Failed to process command, invalid syntax.";
	}


	// gets all commands from specified SQL file
	private boolean getCommandsFromFile(String filename, List<String> commands)
	{		
		BufferedReader br = null;
		FileReader fr = null;
		String command = "";
		String linedata;
		int commentIndex = 0;
		int semicolonIndex = 0;

		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			
			// read a line at a time, watching out for comments
			while ((linedata = br.readLine()) != null) {
				commentIndex = linedata.indexOf("--");
				
				// if there is a comment, only grab the line up to before the comment
				if (commentIndex != -1)
					linedata = linedata.substring(0, commentIndex);
				
				// check if there is a semicolon, now that we have the actual complete command on this line
				semicolonIndex = linedata.trim().indexOf(";");
				
				// if there is a semicolon, grab the command up to that point and append it to the working command
				// NOTE: anything after the (first) semicolon is tossed out 
				if (semicolonIndex != -1) {
					command += linedata.substring(0, semicolonIndex);
					commands.add(command + ";");
					command = "";
				}
				// else if no semicolon, add the linedata to the working command
				else if (linedata.length() > 0) {
					
					// check for .EXIT commands
					if (linedata.toLowerCase().equals(".exit")) {
						commands.add(linedata);
						command = "";
					}
					else
						command += linedata;				
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

		if (commands.size() > 0)
			return true;
		return false;
	}

	
	/*
	 * Cache the list of possible database names, so the program can know if a 
	 * database exists without having to attempt to read from the file system
	 */
	private boolean cacheDatabaseNames() {
		try {
			File dbFolder = new File(buildDatabaseFolderPath(workingDirectory));
			
			if (dbFolder.exists()) {
				File[] listOfFiles = dbFolder.listFiles();

			    for (int i = 0; i < listOfFiles.length; i++) {
			        if (listOfFiles[i].isDirectory()) {
			            databaseNames.add(listOfFiles[i].getName());
			        }
			    }
			}
			// create the db folder if it doesn't exist
			else {
				dbFolder.mkdir();
			}

		}
		catch (Exception e) {
			System.out.println(String.format("!Fatal error while caching database names."));
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	
	// builds the complete path to a database (so tables can be accessed), in a platform-independent way
	private String buildCompleteDatabasePath(String dbName) {
		String unixString = "%1$s/db/%2$s/";
		String windowsString = "%1$s\\db\\%2$s\\";		
		return buildPath(unixString, windowsString, dbName);
	}
	
	//builds db folder, in a platform-independant way
	private String buildDatabaseFolderPath(String workingDir) {
		String unixString = "%1$s/db";
		String windowsString = "%1$s\\db";		
		return buildPath(unixString, windowsString, workingDir);
	}
	
	
	// builds the path to a database, in a platform-independent way
	private String buildDatabasePath(String dbName) {
		String unixString = "%1$s/db/%2$s";
		String windowsString = "%1$s\\db\\%2$s";		
		return buildPath(unixString, windowsString, dbName);
	}

	
	// builds the path to the user's specified sql script, in a platform-independent way
	private String buildSqlFilenamePath(String sqlFilename) {
		String unixString = "%1$s/%2$s";
		String windowsString = "%1$s\\%2$s";		
		return buildPath(unixString, windowsString, sqlFilename);
	}
	
	
	/*
	 * Builds the path to a particular filename, given the unix and windows variations of the 
	 * formatted string representing the filepath. Returns the full-built filepath that is correct
	 * given the architecture that was passed in at the time the ManagementSystem was initialized.
	 * 
	 * NOTE: Assumes the inputted unix/windows strings have the replacement regex (%1$s) for inserting
	 * the working directory and the filename.
	 */
	private String buildPath(String unixString, String windowsString, String filename) {
		if (architecture.equals("unix"))
			return String.format(unixString, workingDirectory, filename);
		else if (architecture.equals("windows"))
			return String.format(windowsString, workingDirectory, filename);
		else {
			System.out.println(String.format("Error - Invalid architecture detected in the ManagementSystem: %1$s", architecture));
			
			// default to unix in case of bad architecture
			return String.format(unixString, workingDirectory, filename);
		}
	}
}
