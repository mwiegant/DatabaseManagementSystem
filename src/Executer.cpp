#include "Executer.h"
#include <iostream>

Executer::Executer()
{
	// not implemented yet
	//this->parser = new Parser();
}

Executer::Executer(Database &db)
{
	this->db = db;
}

Executer::~Executer()
{
	// not implemented yet
}

Database Executer::GetDatabase()
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
	else if (commandVector[0] == "DROP" && commandVector[1] == "TABLE")
		result = ExecuteDropTableCommand(commandVector);
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

	if (db.createTable(commandVector[2]))
		message = "Table " + commandVector[2] + " created.";
	else
		message = "!Failed to create table " + commandVector[2] + " because it already exists.";

	return message;	
}

string Executer::ExecuteDropTableCommand(vector<string> commandVector)
{
	string message;
	
	if (db.dropTable(commandVector[2]))
		message = "Table " + commandVector[2] + " deleted.";
	else
		message = "!Failed to delete " + commandVector[2] + " because it does not exist.";

	return message;	
}

string Executer::ExecuteAlterCommand(vector<string> commandVector)
{
	string message;

	// message = Alter(commandVector); -- Idk what parameters will be here quite yet

	return "TEMP: ALtered table.";
}

string Executer::ExecuteSelectCommand(vector<string> commandVector)
{
	string message;

	// message = Select(commandVector); -- Idk what parameters will be here quite yet

	return "TEMP: Select statement executed.";
}











