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
	cout << "-Executer: ExecuteCommand" << endl;
	// not implemented
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

string Executer::ExecuteCreateTableCommand(vector<string> command)
{
	string message;


}

string Executer::ExecuteCreateDatabaseCommand(vector<string> command)
{
	string message;


}

string Executer::ExecuteDropTableCommand(vector<string> command)
{
	string message;
}

string Executer::ExecuteDropDatabaseCommand(vector<string> command)
{
	string message;
}

string Executer::ExecuteAlterCommand(vector<string> command)
{
	string message;
}

string Executer::ExecuteSelectCommand(vector<string> command)
{
	string message;
}











