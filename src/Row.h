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

	private:

	map<string, string> columns;
};


#endif //DB_ROW