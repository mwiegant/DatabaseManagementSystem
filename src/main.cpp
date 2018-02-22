#define NULL 0

#include <iostream>
#include <string>
#include <cstddef>
#include "ManagementSystem.h"

using namespace std;

bool isValidScriptName(string);


int main(int argc, char **argv)
{
  ManagementSystem *managementSystem = new ManagementSystem();

  // check if the user specified an sql script to run
  if (argc > 1)
  {
    if (isValidScriptName(argv[1]))
    {
      managementSystem->RunInScriptMode(argv[1]);
    } 
    else 
    {      
      cout << "Error - Invalid script specified. Can only process scripts in the format of <name>.sql" << endl;
      
      return -1;
    }

  // otherwise, run in command-line mode, reading one command at a time
  } 
  else 
  {
    managementSystem->RunInCommandLineMode();  
  }

  // de-allocate management system
  delete managementSystem;
  managementSystem = NULL;

  return 0;
}


/*
 * Helper function, to check if the filename is a valid SQL filename.
 *
 * The only criteria for being 'valid' is that the script ends in .sql
 */
bool isValidScriptName(string sqlFilename)
{
  size_t found = sqlFilename.find(".sql");

  // technically only checking if the string contains .sql, 
  // not if it ends in .sql or not
  if (found != string::npos)
    return true;
  return false;
}




