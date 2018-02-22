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

#include <iostream>
#include "stdio.h"
#include <string>
#include <fstream>
#include <algorithm>

#include "Executer.h"
#include "DatabasePersister.h"

using namespace std;

class ManagementSystem
{
  public:
    ManagementSystem();
    ~ManagementSystem();

    void RunInScriptMode(string);
    void RunInCommandLineMode();

  private:
  	bool exitProgram;
  	DatabasePersister *databasePersister;
  	Database *database;
    Executer *executer;

    bool loadDatabase(string dbName);
    bool saveDatabase(Database db);
    void processCommand(string command);    
};






#endif //DB_MANAGEMENT_SYSTEM_H





