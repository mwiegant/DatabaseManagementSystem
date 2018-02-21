#define NULL 0

#include <iostream>
#include "ManagementSystem.h"

using namespace.std;

int main(int argc, char **argv)
{
	ManagementSystem *managementSystem = new ManagementSystem();
	

	delete managementSystem;
	managementSystem = NULL;


return 0;
}
