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
	
	private:
	
	list<Row> rows;
};


#endif //DB_TABLE