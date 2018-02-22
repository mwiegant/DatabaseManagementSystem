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

	private:
	
	list<Table> tables;

};


#endif //DB_DATABASE