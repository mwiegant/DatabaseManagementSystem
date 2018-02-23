#ifndef DB_TABLE
#define DB_TABLE

#include <string>
#include <list>
#include <map>
#include "Row.h"

using namespace std;

class Table
{
	public:
	Table(string tableName);
	~Table();
	Row getRow();
	bool createRow();
	bool deleteRow();
	string getTableName();
	Table& operator = (const Table& other);
	map<string, string> getColumn();
	bool createColumn(string colName, string colType);
	bool deleteColumn();
	
	private:
	
	list<Row> *rows;
	string TableName;
	map<string, string> columns;
};


#endif //DB_TABLE
