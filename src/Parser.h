#ifndef DB_PARSER
#define DB_PARSER

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
#include <cstdlib>
#include <vector>
#include <sstream>
#include <iostream>
#include <iterator>

using namespace std;

class Parser
{
  public:
    Parser();
    ~Parser();
    string ParseCommand(string command);
    vector<string> SplitCommand(string command);
    vector<pair<string,string>> ParseColumnInfo(vector<string> commandVector);

  private:
  	string ValidateCreateStatement(vector<string> command);
  	string ValidateDropStatement(vector<string> command);
  	string ValidateAlterStatement(vector<string> command);
  	string ValidateSelectStatement(vector<string> command);

};


#endif //DB_PARSER