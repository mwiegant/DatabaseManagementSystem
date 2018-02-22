#include "Table.h"

using namespace std;

Table::Table(string tableName)
{
	TableName = tableName;
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




