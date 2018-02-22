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
	vector<string> *commands = new vector<string>();

	// exit program if it fails to load the file
	if (!getCommandsFromFile(sqlFilename, *commands))
	{
		cout << "Error - file does not exist: " << sqlFilename << endl;
		return;
	}

	// run one command at a time from the file
	for (vector<string>::iterator it = commands->begin(); it != commands->end(); ++it)
	{
    	cout << " > " << *it << endl;
    	processCommand(*it);
 	}
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


// process one SQL command
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
		cout << "All done." << endl;
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

	cout << endl;
}


bool ManagementSystem::getCommandsFromFile(string filename, vector<string> &commands)
{
	ifstream fin;
	string linedata;
	bool readTheFile = false;

	// clear input file-stream flags and open the file
	fin.clear();
	fin.open(filename);

	// read all commands from file into a vector, looking out for comments in the file
	while (fin.good())
	{
		readTheFile = true;

		// get one line at a time
		getline(fin, linedata, '\n');

		// get everything before the first comment, if one exists on the current line
		if(linedata.length() != 0)
		{
			size_t index = linedata.find("--");

			// comment at first character on line, skip this entire line
			if(index == 0)
				continue;

			// no comment was found, add the entire line to vector of commands
			if(index == string::npos)
				commands.push_back(linedata);

			// otherwise, comment was found in middle of line so grab the entire line from before the comment
			else
				commands.push_back(linedata.substr(0, index));
		}
	}

	fin.close();

	return readTheFile;
}













