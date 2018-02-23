#ifndef DB_TABLE
#define DB_TABLE

#include <string>
#include <list>
#include "Row.h"

using namespace std;

class Table
{
	public:
	Table();
	~Table();
	Table(string tableName);
	~Table();
	Row getRow();
	bool createRow();
	bool deleteRow();
	string getTableName();
	Table& operator = (const Table& other);
	
	private:
	
	list<Row> rows;
	string TableName;
};


#endif //DB_TABLE
