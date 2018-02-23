#include "ManagementSystem.h"

ManagementSystem::ManagementSystem()
{
	exitProgram = false;
	databasePersister = new DatabasePersister();
	database = NULL;
	databaseNames = NULL;

	// initially, create the executer with default constructor. This allows the user
	// to run database create / drop commands before specifying a database to use
	executer = new Executer();

	parser = new Parser();

	// load list of database names
	if(!loadDatabaseList())
		cout << "Warning - failed to laod database list. Program may not run as intended";
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
	delete parser;

	// this is redundant, but necessary in case it was forgotten elsewhere
	saveDatabaseList();
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
		getline(cin, userInput, '\n');

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
	databasePersister->loadDatabase(dbName, newDb);

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

	// check for a 'create database' or 'drop database' command
	else if (lowercaseUserInput.find("database") != string::npos)
	{
		cout << processDatabaseCommand(lowercaseUserInput);
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


// processes commands to create and drop databases
string ManagementSystem::processDatabaseCommand(string lowercaseCommand)
{
	string dbName;
	vector<string> commandVector;
	commandVector = parser->SplitCommand(lowercaseCommand);

	// check for invalid command syntax
	if(commandVector.size() != 3)
		return "!Failed to process command, invalid syntax.";

	dbName = commandVector[2];

	// try to create database
	if (commandVector[0] == "create" && commandVector[1] == "database")
	{
		// see if database already exists
		if (databaseExists(dbName))
			return "!Failed to create database " + dbName + " because it already exists.";

		databaseNames->push_back(dbName);
		databasePersister->InitializeDatabase(dbName);
		saveDatabaseList();
		return "Database " + dbName + " created.";
	}

	// try to drop database
	else if (commandVector[0] == "drop" && commandVector[1] == "database")
	{
		// see if database does not exist
		if (!databaseExists(dbName))
			return "!Failed to delete " + dbName + " because it does not exist.";

		databaseNames->remove(dbName);
		databasePersister->DropDatabase(dbName);
		saveDatabaseList();
		return "Database " + dbName + " deleted.";
	}

	else
		return "!Failed to process command, invalid syntax.";
}


// gets all commands from specified SQL file
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


// check if the database exists in the databaseList
bool ManagementSystem::databaseExists(string dbName)
{
	list<string>::iterator it;
	for (it = databaseNames->begin(); it != databaseNames->end(); ++it)
	{

//TEMP
cout << "comparing '" << *it << "' against user specified " << dbName << endl;

    	if ((*it).compare(dbName) == 0)
    		return true;
	}

	return false;
}


// loads the list of database names on file
bool ManagementSystem::loadDatabaseList()
{	
	ifstream fin;
	string linedata;
	bool readTheFile = false;
	string path = "db/databases.meta";

	// initialize database list or return the list if it has already been initialized
	if(databaseNames != NULL)
		return true;
	databaseNames = new list<string>();

	// clear input file-stream flags and open the file
	fin.clear();
	fin.open(path);

	// read all database names into a list
	while (fin.good())
	{
		readTheFile = true;

		// get one line at a time
		getline(fin, linedata, '\n');

		if (linedata.length() > 0)
			databaseNames->push_back(linedata);
	}

	fin.close();

	return readTheFile;
}


// saves the list of database names to file
bool ManagementSystem::saveDatabaseList()
{	
	ofstream fout;
	string linedata;
	bool wroteTheFile = false;
	string path = "db/databases.meta";
	list<string>::iterator it;
	
	it = databaseNames->begin();

	// clear output file-stream flags and open the file
	fout.clear();
  	fout.open(path);

	// read all database names into a list
	while (fout.good() && it != databaseNames->end())
	{
		wroteTheFile = true;

		fout << *it << endl;
		it++;
	}

	fout.close();

	return wroteTheFile;
}






