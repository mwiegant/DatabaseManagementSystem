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
	tables = new list<Table>(table);
}

Database::~Database()
{

}

bool Database::createTable(string tableName)
{		
	Table *table = new Table("");
	
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
	for (list<Table>::iterator iterator = tables->begin(), end = tables->end(); iterator != end; iterator++)
	{
		
		if(iterator->getTableName().compare(tableName) == 0)	
		{
			tables->erase(iterator);
			return true;
		}
	}
 return false;
}

string Database::getDatabaseName()
{
	return DatabaseName;
}

bool Database::updateTable(string tableName, Table& table)
{
for (list<Table>::iterator iterator = tables->begin(), end = tables->end(); iterator != end; iterator++)
	{
		
		if(iterator->getTableName().compare(tableName) == 0)	
		{
			*iterator = table;
			return true;
		}
	}
 return false;
}
