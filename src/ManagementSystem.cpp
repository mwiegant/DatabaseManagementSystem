#include "ManagementSystem.h"

ManagementSystem::ManagementSystem()
{
	// not implemented yet
}


ManagementSystem::~ManagementSystem()
{
	// not implemented yet
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
	// not implemented yet

	// do while loop, while user does not enter "exit" or ".EXIT"

		// print the prompt: " > "

		// wait for user input, with cin

		// clean the user input (remove leading / trailing spaces, extra spaces)
			// TODO - create a function for this

		// after grabbing user input, check if it represents a valid command (follows correct syntax)

			// if the command is valid, process the command






}



/*	
 *	Trim extra spaces from the command, if any exist.
 */
void ManagementSystem::trimCommand(string& command)
{
	// int index = 0;
	// string trimmedCommand;

	// // move past any leading spaces
	// while (command[index].compare(" ") == 0)
	// 	index++;

	// trimmedCommand = command.substr(index);

	// // copy the command into another string, excluding duplicate spaces
	// // and any trailing spaces
	// while (index < command.size() - 1)
	// {
	// 	trimmedCommand.append()
	// }
}


/*
 * 	Checks if the given command is syntactically valid. Does not check if the command
 *	is semantically correct.
 *
 *	A command is syntactically correct if:
 *		- it begins with an SQL reserved keyword
 *		- each reserved keyword is either followed by a reserved keyword or a variable
 *		- each opening parenthesis is followed (eventually) by a closing parenthesis
 *		- it ends in a semicolin
 */
bool ManagementSystem::isValidCommand(string command)
{
	// not implemented yet
	return false;
}


// accepts one command, attempts to run it
void ManagementSystem::processCommand(string command)
{
	// not implemented yet
}


void ManagementSystem::grabNextCommand(string tokenizedCommands[])
{
	// not implemented yet
}















