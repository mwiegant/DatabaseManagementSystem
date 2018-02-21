#ifndef DB_MANAGEMENT_SYSTEM_H
#define DB_MANAGEMENT_SYSTEM_H

/*
 * CLASS - ManagementSystem
 *
 * ABOUT - Driver class for running the database management system.
 *   Reads in one command at a time from the command line and decides if
 *   a command is valid. Invalid commands have an error printed on screen.
 *   Valid commands gets processed according to what their instruction is.
 * 
 * AUTHOR - Max Wiegant
 *
 * DATE - Feb 15 2018
 */

#include <cstdio>
#include <string>
#include <fstream>
// #include "Executer.h"
// #include "DatabasePersister.h"

using namespace std;

class ManagementSystem
{
  public:
    ManagementSystem();
    ~ManagementSystem();

    void RunInScriptMode(string);
    void RunInCommandLineMode();

  private:

    











    void trimCommand(string& command);
    bool isValidCommand(string command);
    void processCommand(string command);


    // TODO - may not need this command
    void grabNextCommand(string tokenizedCommands[]);
};






#endif //DB_MANAGEMENT_SYSTEM_H





