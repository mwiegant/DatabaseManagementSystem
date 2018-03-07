#ifndef DB_ROW
#define DB_ROW

#include <string>

using namespace std;

class Row
{

	public:
	
	Row();
	~Row();
	string getRowName();

	private:

	string rowName;
};


#endif //DB_ROW
