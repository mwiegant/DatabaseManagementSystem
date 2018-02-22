#include "Parser.h"

Parser::Parser()
{
	// not implemented yet
}

Parser::~Parser()
{
	// not implemented yet
}

string Parser::ParseCommand(string command)
{
	string result = NULL;
	vector<string> commandVector;

	// Break up command into a array of strings
	commandVector = SplitCommand(command);

	// Check for valid SQL syntax ----- For future projects
	if (commandVector.size() == 0)
		result = "Command was empty";
	else if (commandVector[0] == "CREATE")
		result = ValidateCreateStatement(commandVector);
	else if (commandVector[0] == "DROP")
		result = ValidateDropStatement(commandVector);
	else if (commandVector[0] == "ALTER")
		result = ValidateAlterStatement(commandVector);
	else if (commandVector[0] == "SELECT")
		result = ValidateSelectStatement(commandVector);
	else
		result = commandVector[0] + " is not a valid SQL command\n\n";

	return result;
}

vector<string> Parser::SplitCommand(string command)
{
	cout << "-Parser: SplitCommand" << endl;
	istringstream iss(command);
	vector<string> commandVector = {istream_iterator<string>{iss},
									istream_iterator<string>{}};

	return commandVector;
}

string Parser::ValidateCreateStatement(vector<string> command)
{
	return "valid";
}

string Parser::ValidateDropStatement(vector<string> command)
{
	return "valid";
}

string Parser::ValidateAlterStatement(vector<string> command)
{
	return "valid";
}

string Parser::ValidateSelectStatement(vector<string> command)
{
	return "valid";
}





