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
#include <cstring>
#include "stdio.h"
#include <string>
#include <fstream>
#include <algorithm>
#include <vector>

#include "Parser.h"
#include "Executer.h"
#include "DatabasePersister.h"

using namespace std;

class ManagementSystem
{
  public:
    ManagementSystem();
    ~ManagementSystem();

    void RunInScriptMode(string sqlFilename);
    void RunInCommandLineMode();

  private:
  	bool exitProgram;
  	DatabasePersister *databasePersister;
  	Database *database;
    Executer *executer;
    Parser *parser;
    list<string> *databaseNames;

    bool loadDatabase(string dbName);
    bool saveDatabase(Database db);    
    void processCommand(string command);
    string processDatabaseCommand(string lowercaseCommand);
    bool getCommandsFromFile(string filename, vector<string> &commands);
    bool databaseExists(string dbName);
    bool loadDatabaseList();
    bool saveDatabaseList();
};






#endif //DB_MANAGEMENT_SYSTEM_H





