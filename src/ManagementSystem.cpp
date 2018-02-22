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
	bool exit = false;
	string userInput;


	// do while loop, while user does not enter "exit" or ".EXIT"
	do
	{
		
		// prompt user for input
		cout << " > ";
		cin >> userInput;

		// check for an exit command
		if (userInput.find(".EXIT") == string::npos || 
			userInput.find("exit") == string::npos)
		{
			cout << "All done." << endl;
			exit = true;
			continue;
		}

		else
		{
			
		}

	} while (!exit);

		// print the prompt: " > "

		// wait for user input, with cin

		// clean the user input (remove leading / trailing spaces, extra spaces)
			// TODO - create a function for this

		// after grabbing user input, check if it represents a valid command (follows correct syntax)

			// if the command is valid, process the command



}





void ManagementSystem::grabNextCommand(string tokenizedCommands[])
{
	// not implemented yet
}















