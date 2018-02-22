# Max Wiegant
# 2 - 15 - 18
# Make Peace, not Bugs

CC = g++
DEBUG = -g
CFLAGS = -c -std=c++11
LFLAGS = -std=c++11
SOURCE = src/

dbms : $(SOURCE)main.cpp Row.o Table.o Database.o DatabasePersister.o Parser.o Executer.o ManagementSystem.o
	$(CC) $(LFLAGS) $(SOURCE)main.cpp Row.o Table.o Database.o DatabasePersister.o Parser.o Executer.o ManagementSystem.o -o dbms

ManagementSystem.o : $(SOURCE)ManagementSystem.cpp
	$(CC) $(CFLAGS) $(SOURCE)ManagementSystem.cpp

Executer.o : $(SOURCE)Executer.cpp
	$(CC) $(CFLAGS) $(SOURCE)Executer.cpp

Parser.o : $(SOURCE)Parser.cpp
	$(CC) $(CFLAGS) $(SOURCE)Parser.cpp

DatabasePersister.o : $(SOURCE)DatabasePersister.cpp
	$(CC) $(CFLAGS) $(SOURCE)DatabasePersister.cpp

Database.o : $(SOURCE)Database.cpp
	$(CC) $(CFLAGS) $(SOURCE)Database.cpp

Table.o : $(SOURCE)Table.cpp
	$(CC) $(CFLAGS) $(SOURCE)Table.cpp

Row.o : $(SOURCE)Row.cpp
	$(CC) $(CFLAGS) $(SOURCE)Row.cpp


clean:
	\rm *.o dbms

