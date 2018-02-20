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
 *
 *      ** a re-write of this documentation, with author and history, is required **
 */

// TODO - clean out these include statements
#include <time.h>
#include <sys/time.h>
#include <cstdio>
#include <string>
#include <fstream>



using namespace std;

class ManagementSystem
{
  public:
    ManagementSystem();
    ~ManagementSystem();

    // required; used to configure file read (additional functionality) or read from command line (default functionality)
    bool Initialize(bool readFromFile, string fileName);

    // used to begin accepting commands
    bool Run();

  private:
    void grabNextCommand(string tokenizedCommands[]);
    bool isValidSyntax(string tokenizedCommands[]);


};

#endif //DB_MANAGEMENT_SYSTEM_H
