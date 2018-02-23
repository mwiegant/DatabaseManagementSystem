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
	
	~Database();

	bool createTable(string tableName);

	bool getTable(string tableName, Table& table);
	
	bool dropTable(string tableName);

	string getDatabaseName();

	private:
	
	list<Table> *tables;

	string DatabaseName;

};


#endif //DB_DATABASE
