#include "ManagementSystem.h"

ManagementSystem::ManagementSystem()
{
	exitProgram = false;
	databasePersister = new DatabasePersister();
	database = NULL;

	// initially, create the executer with default constructor. This allows the user
	// to run database create / drop commands before specifying a database to use
	executer = new Executer();
}


ManagementSystem::~ManagementSystem()
{
	// check if a database object was already loaded, and if save it to the filesystem
	if (database != NULL)
	{
		// databasePersister->saveDatabase((*database));

		delete database;
		delete executer;
	}

	delete databasePersister;
}


// reads from an sql file, running one command at a time, until the end of the file
void ManagementSystem::RunInScriptMode(string sqlFilename)
{
	// not implemented yet

	// will implement this after I get command line mode working.
}


// runs the program indefinitely, running a command at a time
void ManagementSystem::RunInCommandLineMode()
{
	string userInput;

	// continually loop while user does not enter "exit" or ".EXIT"
	do
	{		
		// prompt user for input
		cout << " > ";
		cin >> userInput;

		processCommand(userInput);

		cout << endl;
	} 
	while (!exitProgram);
}


/*
 *	Wrapper function for calling DatabasePersister to try to load database.
 *
 *	Replaces the existing database and executer objects, if they exist.
 * 	Saves the existing database to the file system before replacing it.
 */
bool ManagementSystem::loadDatabase(string dbName)
{
	Database *newDb;
	// Database *newDb = databasePersister->loadDatabase(dbName);

	// check if the database actually exists, and return error if it does not
	if (newDb == NULL)
		return false;

	// check if a database object was already loaded, and if save it to the filesystem
	// 	(also replace the old database and executer objects)
	if (database != NULL)
	{
		// databasePersister->saveDatabase((*database));

		delete database;
		delete executer;
	}

	database = newDb;
	executer = new Executer(*database);
	
	return true;
}



void ManagementSystem::processCommand(string command)
{
	string lowercaseUserInput;	
	char *token;

	// command to lowercase
	lowercaseUserInput = command;
	transform(lowercaseUserInput.begin(), lowercaseUserInput.end(), lowercaseUserInput.begin(), ::tolower);

	// check for an exit command
	if (lowercaseUserInput.find(".exit") != string::npos || 
		lowercaseUserInput.find("exit") != string::npos)
	{
		cout << "All done.";
		exitProgram = true;
		return;
	}

	// check for a 'use database' command
	else if (lowercaseUserInput.find("use") != string::npos)
	{			
		char *temp = new char[lowercaseUserInput.length() + 1];
		strcpy(temp, lowercaseUserInput.c_str());

		token = strtok(temp, " "); // grabs the 'use' word
		token = strtok(NULL, " "); // ...and now we've grabbed the database name

		if(!loadDatabase(token))
			cout << "!Failed to load '" << token << "' database.";
		else
			cout << "Using database " << token << ".";
	}

	// otherwise, send command to Executer and let it handle the command
	else
	{
		if(executer == NULL)
			cout << "!Failed - must specify a database before accepting any commands.";

		// pass the original command to the executer
		cout << executer->ExecuteCommand(command);
	}
}















