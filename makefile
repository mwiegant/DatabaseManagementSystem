# Max Wiegant
# 2 - 15 - 18
# Make Peace, not Bugs

CC = g++
DEBUG = -g
CFLAGS = -c -std=c++11
LFLAGS = -std=c++11
SOURCE = src/

dbms : $(SOURCE)main.cpp DatabasePersister.o Parser.o Executer.o ManagementSystem.o
	$(CC) $(LFLAGS) $(SOURCE)main.cpp DatabasePersister.o Parser.o Executer.o ManagementSystem.o -o dbms

ManagementSystem.o : $(SOURCE)ManagementSystem.cpp
	$(CC) $(CFLAGS) $(SOURCE)ManagementSystem.cpp

Executer.o : $(SOURCE)Executer.cpp
	$(CC) $(CFLAGS) $(SOURCE)Executer.cpp

Parser.o : $(SOURCE)Parser.cpp
	$(CC) $(CFLAGS) $(SOURCE)Parser.cpp

DatabasePersister.o : $(SOURCE)DatabasePersister.cpp
	$(CC) $(CFLAGS) $(SOURCE)DatabasePersister.cpp

clean:
	\rm *.o dbms

