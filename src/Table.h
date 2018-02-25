#ifndef DB_TABLE
#define DB_TABLE

#include <string>
#include <list>
#include <map>
#include "Row.h"
#include "iostream"

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
	map<string, string> getColumns() const;
	bool createColumn(string colName, string colType);
	bool deleteColumn(string colName);
	
	private:
	
	list<Row> *rows;
	string TableName;
	map<string, string> *columns;
};


#endif //DB_TABLE
