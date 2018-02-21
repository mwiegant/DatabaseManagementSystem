# Max Wiegant
# 2 - 15 - 18
# Make Peace, not Bugs

CC = g++
DEBUG = -g
CFLAGS = -c -std=c++11 -pthread
LFLAGS = -std=c++11 -pthread
SOURCE = src/

dbms : $(SOURCE)main.cpp ManagementSystem.o
	$(CC) $(LFLAGS) $(SOURCE)main.cpp ManagementSystem.o -o dbms

ManagementSystem.o : $(SOURCE)ManagementSystem.cpp
	$(CC) $(CFLAGS) $(SOURCE)ManagementSystem.cpp

clean:
	\rm *.o dbms

