package dbms;

 import dbms.ManagementSystem;

public class DBMS {

	private static final String DEFAULT_WDIR = System.getProperty("user.dir");
	private static final String DEFAULT_ARCH = "unix";
	private static String wdir = null;
	private static String arch = null;
	private static String filename = null;
	
	public static void main(String[] args) {				
		ManagementSystem managementSystem;

		// process any arguments from the command line, and also initialize program variables as needed
		processArguments(args);
		
		managementSystem = new ManagementSystem(wdir, arch);
		
		if (filename != null)
			managementSystem.RunInScriptMode(filename);
		else
			managementSystem.RunInCommandLineMode();
	}
	
	
	/*
	 * Process arguments passed in at the command line or through a run configuration.
	 * 
	 * All arguments should be key-value pairs, except for the optional argument where
	 * the user specifies an SQL filename to run the program with.
	 * 
	 * Consider any error thrown here to be non-fatal. Truthfully, errors related to
	 * the SQL filename should perhaps be fatal.
	 */
	private static void processArguments(String[] args) {
		String[] tokens;
		String key, value;
		
		for (String arg : args) {
			
			// assume that the one argument without an '=' is the optional SQL filename to run the program with
			if (!arg.contains("=")) {				
				if (arg.endsWith(".sql"))
					filename = arg;
				else
					System.out.println("Error - Invalid script specified. Can only process scripts in the format of <name>.sql");
			}
			// process all other arguments, which are assumed to be in the format of: key=value
			else {
				tokens = arg.split("=");
				key = tokens[0];
				value = tokens[1];
				
				switch (key) {
				case "wdir":
					wdir = value;
					break;
					
				case "arch":
					if (value.equals("unix") || value.equals("windows"))
						arch = value;
					else
						System.out.println(String.format("Error - Invalid value for 'arch': %1$s", value));
					break;
					
				default:
					System.out.println(String.format("Error - Invalid program argument: %1$s", arg));
				}
			}			
		}
		
		// Set default values for any program parameters that did not get initialized yet
		if (wdir == null)
			wdir = DEFAULT_WDIR;
		if (arch == null)
			arch = DEFAULT_ARCH;
	}
	
	
}
 