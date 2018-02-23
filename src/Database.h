#ifndef DB_DATABASE
#define DB_DATABASE

#include <string>
#include "Table.h"
#include <list>

using namespace std;

class Database
{
	public:
	
	Database();

	Database(list<Table> table);
	
	~Database();

	bool createTable(string tableName);

	bool getTableQuery(string tableName, Table& table);
	
	bool dropTable(string tableName);

	string getDatabaseName();

	bool updateTable(string tableName, Table& table);

	private:
	
	list<Table> *tables;

	string DatabaseName;

};


#endif //DB_DATABASE
