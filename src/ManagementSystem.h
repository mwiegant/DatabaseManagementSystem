#ifndef DB_MANAGEMENT_SYSTEM_H
#define DB_MANAGEMENT_SYSTEM_H

#include <time.h>
#include <sys/time.h>
#include <cstdio>
#include <string>
#include <fstream>
#include "database.h"
#include <list>
#include <map>

using namespace std;

class ManagementSystem
{
	public:
	ManagementSystem();
	~ManagementSystem();
	
	bool Initialize(bool readFromFile, string fileName);

	bool Run();
	
	private:
	
	void grabNextCommand(string tokenizedCommands[]);
	
	boool isValidSyntax(string tokenizedCommands[]);

	database db;
};

#endif
