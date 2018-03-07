# Database Management System
Custom DBMS

# How to build

To build the DBMS executable, navigate to the build directory and run the **make** command.

```
	make
```

# How to run

There are two ways to use our custom DBMS. The first way is by passing in the filename of an SQL script in the same directory as the executable, like so:

```
	./dbms my_script.sql
```

The other way to use the custom DBMS is to call the executable with no arguments, thus entering 'command line' mode:

```
	./dbms
```