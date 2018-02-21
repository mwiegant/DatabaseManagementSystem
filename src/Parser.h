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

using namespace std;

class Parser
{
  public:
    Parser();
    ~Parser();

  private:

};


#endif //DB_PARSER





