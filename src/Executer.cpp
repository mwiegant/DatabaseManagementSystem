#include "Executer.h"
#include <iostream>

Executer::Executer()
{
	// not implemented yet
	//this->parser = new Parser();
}

Executer::Executer(database &db)
{
	this->db = db;
}

Executer::~Executer()
{
	// not implemented yet
}

database Executer::GetDatabase()
{
	return db;
}

string Executer::ExecuteCommand(string command)
{
	string result;
	vector<string> commandVector;

	// Send command to parser to check syntax
	result = parser.ParseCommand(command);
	commandVector = parser.SplitCommand(command);

	if (result != "valid")
		result = "INVALID SQL SYNTAX: " + result;

	// Otherwise execute command
	else if (commandVector[0] == "CREATE" && commandVector[1] == "TABLE")
		result = ExecuteCreateTableCommand(commandVector);
	else if (commandVector[0] == "CREATE" && commandVector[1] == "DATABASE")
		result = ExecuteCreateDatabaseCommand(commandVector);	
	else if (commandVector[0] == "DROP" && commandVector[1] == "TABLE")
		result = ExecuteDropTableCommand(commandVector);
	else if (commandVector[0] == "DROP" && commandVector[1] == "DATABASE")
		result = ExecuteDropDatabaseCommand(commandVector);
	else if (commandVector[0] == "ALTER")
		result = ExecuteAlterCommand(commandVector);
	else if (commandVector[0] == "SELECT")
		result = ExecuteSelectCommand(commandVector);
	else
		result = "INVALID SQL COMMAND";

	// Pass back string message
	return result;
}

string Executer::ExecuteCreateTableCommand(vector<string> commandVector)
{
	string message;

	/*// Check if table already exists
	for (int i = 0; i < db.tables.size(); i++)
	{
		Table 
		if (db.tables[i]->name == commandVector[2])
			message = "!Failed to create table " + commandVector[2] + " because it already exists.";
	}*/

	message = "Table " + commandVector[2] + " created.";

	return message;	
}

string Executer::ExecuteCreateDatabaseCommand(vector<string> commandVector)
{
	string message;

	message = "Database " + commandVector[2] + " created.";

	return message;
}

string Executer::ExecuteDropTableCommand(vector<string> commandVector)
{
	string message;

	message = "Table " + commandVector[2] + " deleted.";

	return message;	
}

string Executer::ExecuteDropDatabaseCommand(vector<string> commandVector)
{
	string message;

	message = "Database " + commandVector[2] + " deleted.";

	return message;
}

string Executer::ExecuteAlterCommand(vector<string> commandVector)
{
	string message;

	return "TEMP: ALtered table.";
}

string Executer::ExecuteSelectCommand(vector<string> commandVector)
{
	string message;

	return "TEMP: Select statement executed.";
}











