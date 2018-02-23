#ifndef DB_DATABASE_PERSISTER_H
#define DB_DATABASE_PERSISTER_H

/*
 * CLASS - DatabasePersister
 *
 * ABOUT - Saves and loads database objects to and from file.
 *   Also responsible for creating new database entries on the file
 *   system and deleting database entries from the file system.
 * 
 * AUTHOR - Max Wiegant
 *
 * DATE - Feb 15 2018
 */

#include <cstdlib>
#include <string>
#include <cstring>
#include <fstream>
#include <list>
#include <iostream>
#include "Database.h"
#include "Table.h"

using namespace std;

class DatabasePersister
{
  public:
    DatabasePersister();
    ~DatabasePersister();

    void loadDatabase(string databaseName, Database *db);
    bool saveDatabase(Database database);
    void InitializeDatabase(string databaseName);
    void DropDatabase(string databaseName);

  private:
  	bool loadTablesMeta(string dbName, list<string> *tableNames);
  	bool loadTable(string dbName, string tableName, Table *table);
};


#endif //DB_DATABASE_PERSISTER_H


