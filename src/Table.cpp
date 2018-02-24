#include "Table.h"

using namespace std;

Table::Table(string tableName)
{
	TableName = tableName;
	rows = new list<Row>();
	columns = new map<string, string>();
}

Table::~Table()
{

}

Row Table::getRow()
{
	Row row;
	return row;
}


bool Table::createRow()
{
	// temp
	return true;
}

bool Table::deleteRow()
{
	// temp
	return true;
}

string Table::getTableName()
{
	return TableName;
}

Table& Table::operator = (const Table& other)
{
	if (this != &other)
	{
		*columns = other.getColumns();
		TableName = other.TableName;
	}
return *this;
}

map<string, string> Table::getColumns() const
{
	return *columns;
}

bool Table::createColumn(string colName, string colType)
{
	columns->insert(pair<string, string>(colName, colType));
}

bool Table::deleteColumn(string colName)
{
	map<string, string>::iterator iterator;
	iterator = columns->find(colName);
	columns->erase(iterator);
}





