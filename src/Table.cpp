#include "Table.h"

using namespace std;

Table::Table(string tableName)
{
	TableName = tableName;
	rows = new list<Row>();
}

Table::~Table()
{

}

Row Table::getRow()
{

}


bool Table::createRow()
{

}


bool Table::deleteRow()
{

}

string Table::getTableName()
{
	return TableName;
}

Table& Table::operator = (const Table& other)
{
	if (this != &other)
	{
		TableName = other.TableName;
	}
return *this;
}

map<string, string> Table::getColumns()
{
	return columns;
}

bool Table::createColumn(string colName, string colType)
{
	columns.insert(pair<string, string>(colName, colType));
}

bool Table::deleteColumn(string colName)
{
	map<string, string>::iterator iterator;
	iterator = columns.find(colName);
	columns.erase(iterator);
}



