#include "DatabasePersister.h"

DatabasePersister::DatabasePersister()
{

}


DatabasePersister::~DatabasePersister()
{

}


// loads an empty database object with Tables and table data from memory
Database* DatabasePersister::loadDatabase(string databaseName)
{
	list<string> *tableNames;
	list<Table> *tables;
	Database *db;

	db = nullptr;
	tableNames = new list<string>();
	tables = new list<Table>();

	// load the database's table.meta file, which contains the tables to load
	if(loadTablesMeta(databaseName, tableNames))
	{
		list<string>::iterator tableNameIterator;

		// for each table name
		for (tableNameIterator = tableNames->begin(); tableNameIterator != tableNames->end(); ++tableNameIterator)
		{
			Table newTable(*tableNameIterator);
			
			// load table (print an error if table load fails, then move on to next table)
	    	if(loadTable(databaseName, *tableNameIterator, &newTable))
    		{
    			// save table to list
    			tables->push_back(newTable);
    		}
    		else
    		{
    			cout << "Error - failed to load table " << *tableNameIterator << " from database " << databaseName << "." << endl;
    		}
		}
	}
	// else, failed to load table.meta file
	else
	{
		cout << "Error - failed to load table.meta file from database " << databaseName << "." << endl;
		db = new Database();
		return db;
	}
	

	if (tables->size() > 0)
	{
		// create new database with tables after all tables have been loaded
		db = new Database(*tables);
	}
	else
	{
		db = new Database();
	}

	return db;
}


bool DatabasePersister::saveDatabase(Database database)
{
	// temp
	return false;
}


// initializes a new database by setting up its directory structure
void DatabasePersister::InitializeDatabase(string databaseName)
{
	string command = "mkdir db/" + databaseName + " && touch db/" + databaseName + "/tables.meta";

	// make database directory and the tablesmeta file
	system(command.c_str());
}

void DatabasePersister::DropDatabase(string databaseName)
{
	string command = "rm -rf db/" + databaseName;

	// recursively delete database directory
	system(command.c_str());
}


// load the tables.meta file for the specified database
bool DatabasePersister::loadTablesMeta(string dbName, list<string> *tableNames)
{
	ifstream fin;
	string linedata;
	bool readTheFile = false;
	string path = "db/" + dbName + "/tables.meta";

	try
	{
		// clear input file-stream flags and open the file
		fin.clear();
		fin.open(path);

		// read all database names into a list
		while (fin.good())
		{
			readTheFile = true;

			// get one line at a time
			getline(fin, linedata, '\n');

			if (linedata.length() > 0)
				tableNames->push_back(linedata);
		}

		fin.close();
	}
	catch (...)
	{
		return false;
	}
		
	return readTheFile;
}	
	

bool DatabasePersister::loadTable(string dbName, string tableName, Table *table)
{
	ifstream fin;
	string colName, colType;
	bool readTheFile = false;
	string path = "db/" + dbName + "/" + tableName + "/schema.meta";

	try
	{
		// clear input file-stream flags and open the file
		fin.clear();
		fin.open(path);

		// read all database names into a list
		while (fin.good())
		{
			readTheFile = true;

			// get one line at a time
			getline(fin, colName, ' ');
			getline(fin, colType, '\n');

			if(colName.length() > 0 && colType.length() > 0)
				table->createColumn(colName, colType);
		}

		fin.close();
	}
	catch (...)
	{
		return false;
	}

	return readTheFile;
}








