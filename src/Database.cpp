#include "Database.h"
#include <string>
#include <list>
#include <iostream>
#include <stdlib.h>
#include <stdio.h>

using namespace std;

Database::Database()
{
	tables = new list<Table>();
}

Database::~Database()
{

}

bool Database::createTable(string tableName)
{
	Table *table = new Table(tableName);
	tables->push_back(*table);
	return true;
}

bool Database::getTable(string tableName, Table& table)
{
	for (list<Table>::iterator iterator = tables->begin(), end = tables->end(); iterator != end; iterator++)
	{
		if((*iterator).getTableName().compare(tableName) == 0)	
		{
			table = *iterator;
			return true;
		}
	}
return false;
}
	
bool Database::dropTable(string tableName)
{
	Table *temp;
	getTable(tableName, *temp);
	delete temp;
	return true;
}

string Database::getDatabaseName()
{
	return DatabaseName;
}
