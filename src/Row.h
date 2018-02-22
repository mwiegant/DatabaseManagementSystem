#ifndef DB_ROW
#define DB_ROW

#include <string>
#include <map>

using namespace std;

class Row
{

	public:
	
	Row();
	~Row();
	map<string, string> getColumn();
	bool createColumn();
	bool deleteColumn();
	string getRowName();

	private:

	map<string, string> columns;
	string rowName;
};


#endif //DB_ROW
