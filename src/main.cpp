#define NULL 0

#include <iostream>

#include "ManagementSystem.h"

using namespace std;


int main(int argc, char **argv)
{
  ManagementSystem *managementSystem = new ManagementSystem();

  // initialize the database management system
  // if( !managementSystem->Initialize(false, NULL) )
  // {
  //   cout << "Error - Failed to initialize the dbms." << endl;
  //   return -2;
  // }
  
  cout << "Hello world!!!" << endl;

  
  // de-allocate management system
  delete managementSystem;
  managementSystem = NULL;



  return 0;
}