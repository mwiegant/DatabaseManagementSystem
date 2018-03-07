package dbms;

// import dbms.ManagementSystem;

public class DBMS {

	public static void main(String[] args) {
		// variable initialization
		
		// check if the user specified an sql script to run
		if(args.length > 0) {
			System.out.println("Script mode initiated. Script name: " + args[0]);
		}
		else {
			System.out.println("Command line mode initiated.");
		}
		
		// for now, that's all I'm trying to do
	}
	
	
}
 