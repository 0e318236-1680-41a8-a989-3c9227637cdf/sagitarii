package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.List;

public class MainLog {
	private static MainLog instance;
	private List<LogItem> log;
	
	
	private MainLog() {
		log = new ArrayList<LogItem>();
	}
	
	public List<LogItem> getLog() {
		return new ArrayList<LogItem>( log );
	}
	
	public List<LogItem> getLogByExecutor( String executor ) {
		List<LogItem> result = new ArrayList<LogItem>();
		for ( LogItem li : getLog()  ) {
			if ( li.getExecutorAlias().equals( executor ) ) {
				result.add( li );
			}
		}
		return result;
	}

	public List<LogItem> getLogByNode( String node ) {
		List<LogItem> result = new ArrayList<LogItem>();
		for ( LogItem li : getLog()  ) {
			if ( li.getMacAddress().equals( node ) ) {
				result.add( li );
			}
		}
		return result;
	}

	
	public static MainLog getInstance() {
		if ( instance == null ) {
			instance = new MainLog();
		}
		return instance;
	}
	
	public synchronized void storeLog(String taskId, String executorAlias, String exitCode, String macAddress, List<String> console, List<String> execLog) {
		LogItem li = new LogItem( taskId,  executorAlias,  exitCode,  macAddress, console,  execLog);
		if ( log.size() > 500 ) {
			log.clear();
		}
		log.add( li );
	}
	

}
