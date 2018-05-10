package dbms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
	DatabasePersister persister = new DatabasePersister();
	private List<String> locks = null;
	private String dbPath;
	
	public Transaction(String databasePath) {
		locks = new ArrayList<String>();
		dbPath = databasePath;
	}
	
	public void addLock(String tableName) {
		// don't add lock if one already exists
		if (locks.contains(tableName))
			return;
		
		locks.add(tableName);
		
		// make the lock file
		try {
			lockTable(tableName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasLocks() {
		if (locks.size() > 0)
			return true;
		return false;
	}
	
	public boolean isLocked(String tableName) {
		List<String> lockedTables = getLockedTables();		
		String name = String.format("%1$s_lock", tableName);
		
		return lockedTables.contains(name);
	}
	
	/*
	 * Commits any transactions that have been done to the file system.
	 * 
	 * The 'startup' bool allows the user to commit transactions right after 
	 * the Transaction object was created, but should always be false in all 
	 * other cases.
	 */
	public void commitTransactions(Database db, boolean startup) {
		// if no files are locked, then there is nothing to commit and this is therefore an abort
		if (!startup && locks.size() == 0) {
			System.out.println("Transaction abort.");
			return;
		}
		
		persister.saveDatabase(dbPath, db);
		
		removeAllLocks();
	}	
	
	
	/*
	 * Remove all locks that this Transaction instance placed on the database
	 */
	private void removeAllLocks() {
		if (locks.size() == 0)
			return;
		
		for (String lockName : locks) {
			String lockPath = dbPath + lockName + "_lock";
			File lockFile = new File(lockPath);
			
			if (lockFile.exists()) {
				lockFile.delete();				
			}
		}
		
		// remove all locks from the list
		locks.clear();
	}
	
	/*
	 * Grabs all locked table names from the specified database path.
	 */
	private List<String> getLockedTables() {		
		List<String> lockedTables = new ArrayList<String>();
		
		try {
			File dbFolder = new File(dbPath);
			
			if (dbFolder.exists()) {
				File[] listOfFiles = dbFolder.listFiles();

			    for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						if (listOfFiles[i].getName().endsWith("_lock"))
							lockedTables.add(listOfFiles[i].getName());
					}
			    }
			}
		}
		catch (Exception e) {
			System.out.println(String.format("!Fatal error while grabbing locked table names."));
			e.printStackTrace();
		}
		
		return lockedTables;
	}
	
	/*
	 * Creates a table lock
	 */
	private void lockTable(String tableName) throws IOException {
		String lockname = String.format("%1$s_lock", tableName);
		String lockPath = dbPath + lockname;
				
		File dbDirectory = new File(dbPath);
        File lockFile = new File(lockPath);
        
        // only lock the table if the db directory exists
        if (dbDirectory.exists()) {
        		lockFile.createNewFile();
        }
	}
}
