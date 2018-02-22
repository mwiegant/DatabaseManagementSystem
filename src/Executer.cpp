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
	else if (commandVector[0] == "CREATE")
		result = ExecuteCreateCommand(commandVector);
	else if (commandVector[0] == "DROP")
		result = ExecuteDropCommand(commandVector);
	else if (commandVector[0] == "ALTER")
		result = ExecuteAlterCommand(commandVector);
	else if (commandVector[0] == "SELECT")
		result = ExecuteSelectCommand(commandVector);

	// Pass back string message
	return result;
}

string Executer::ExecuteCreateCommand(vector<string> command)
{

}

string Executer::ExecuteDropCommand(vector<string> command)
{

}

string Executer::ExecuteAlterCommand(vector<string> command)
{

}

string Executer::ExecuteSelectCommand(vector<string> command)
{

}











