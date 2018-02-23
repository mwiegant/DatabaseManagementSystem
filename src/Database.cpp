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

Database::Database(list<Table> table)
{
	for (list<Table>::iterator iterator = table.begin(), end = table.end(); iterator != end; iterator++)
	{
		createTable((*iterator).getTableName());
	}
}

Database::~Database()
{

}

bool Database::createTable(string tableName)
{		
	Table *table;

	if (getTableQuery(tableName, *table) == false)
	{
		table = new Table(tableName);
		tables->push_back(*table);
		return true;
	}
return false;
}

bool Database::getTableQuery(string tableName, Table& table)
{
	for (list<Table>::iterator iterator = tables->begin(), end = tables->end(); iterator != end; iterator++)
	{
		if(iterator->getTableName().compare(tableName) == 0)	
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
	if (getTableQuery(tableName, *temp) == true)
	{
	delete temp;
	return true;
	}
return false;
}

string Database::getDatabaseName()
{
	return DatabaseName;
}

bool Database::updateTable(string tableName, Table& table)
{
	
}
