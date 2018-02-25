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
	string result;
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
	command.pop_back(); 
	istringstream iss(command);
	vector<string> commandVector = {istream_iterator<string>{iss},
									istream_iterator<string>{}};

	// If a create statement with column info, format the column info
	if (commandVector.size() > 3 && commandVector[0] == "CREATE")
	{
		// Remove the left paranthese of column info
		commandVector[3] = commandVector[3].erase(0,1);
		// Remove the last parenthese of column info
		commandVector[commandVector.size() - 1].pop_back();

		vector<string>::iterator it;
		int i = 0;
		for(it = commandVector.begin(); it != commandVector.end(); it++,i++ )    
		{
			string temp = commandVector[i];
    		size_t found = temp.find_first_of(',');

    		while (found!=std::string::npos)
  			{
   				temp[found]=' ';
    			found=temp.find_first_of(",",found+1);
  			}
  			commandVector[i] = temp;
   		}
	}

	return commandVector;
}

vector<pair<string,string>> Parser::ParseColumnInfo(vector<string> commandVector)
{
	vector<pair<string,string>> columnInfo;

	for (int i = 3; i < commandVector.size(); i+=2)
	{
		columnInfo.push_back(make_pair(commandVector[i], commandVector[i+1]));
	}
	return columnInfo;
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





