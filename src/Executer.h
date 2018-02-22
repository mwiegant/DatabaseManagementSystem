#ifndef DB_EXECUTER
#define DB_EXECUTER

/*
 * CLASS - Executer
 *
 * ABOUT - Driver class that executes sql commands
 *          and modifies the DB in memory according
 *          to the command.
 * 
 * AUTHOR - Saulius Baublys
 *
 * DATE - Feb 15 2018
 */

#include "Parser.h"
#include "Database.h"
#include <cstdio>
#include <string>
#include <fstream>

using namespace std;

class Executer
{
  public:
    Executer();
    Executer(Database &db);
    ~Executer();
    Database GetDatabase();
    string ExecuteCommand(string command);

  private:
  	string ExecuteCreateTableCommand(vector<string> command);
  	string ExecuteCreateDatabaseCommand(vector<string> command);
  	string ExecuteDropTableCommand(vector<string> command);
  	string ExecuteDropDatabaseCommand(vector<string> command);
  	string ExecuteAlterCommand(vector<string> command);
  	string ExecuteSelectCommand(vector<string> command);

  	Database db;
  	Parser parser;
};

#endif //DB_EXECUTER