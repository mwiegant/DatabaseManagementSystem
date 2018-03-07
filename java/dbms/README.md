# Pre-Requisites

- install java
- install the JRE
- install Eclipse (I installed Eclipse Oxygen, which appears to be the latest Eclipse)

*Note: I setup my eclipse to have a dark color-scheme ("theme"). I can show you how to do this, if you are interested.*

# Steps to build the jar

-Setup a new run configuration in Eclipse, making sure to specify dbms.DBMS as the Main Class. Select 'apply' after editing the run configuration.
- Right click the project and select 'Export...'.
- Under the Java folder, select 'Runnable JAR file'.
- In the next dialog that appears, select the run configuration you created earlier as the 'Launch Configuration'.
- Select an Export location (this is the place where the jar file will be placed). Select the folder where our ./dbms executable is located. (For me, this was in Documents/DatabaseProject)
- Select 'Finish'. Also, overwrite the jar file if it exists (You will be prompted to, if it exists). If you have any errors, be sure to google them! Google will guide you.

# How to Run

To run the jar, go to the directory where the ./dbms (and now your jar) file is located at. From that directory, type:

	./dbms <sql_file>
	
(You may leave out the **<sql_file>**, this is an optional parameter.

*Note: I had to do some hacky stuff to make this work :) So long as it does though, who cares?*