package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.List;

public class MainLog {
	private static MainLog instance;
	private List<ReceivedData> log;
	
	
	private MainLog() {
		log = new ArrayList<ReceivedData>();
	}
	
	public List<ReceivedData> getLog() {
		return new ArrayList<ReceivedData>( log );
	}
	
	public List<ReceivedData> getLogByExecutor( String executor ) {
		List<ReceivedData> result = new ArrayList<ReceivedData>();
		for ( ReceivedData rd : getLog()  ) {
			if ( rd.getActivity().getExecutorAlias().equals( executor ) ) {
				result.add( rd );
			}
		}
		return result;
	}

	public List<ReceivedData> getLogByNode( String node ) {
		List<ReceivedData> result = new ArrayList<ReceivedData>();
		for ( ReceivedData rd : getLog()  ) {
			if ( rd.getMacAddress().equals( node ) ) {
				result.add( rd );
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
	
	public synchronized void storeLog( ReceivedData rd ) {
		if ( log.size() > 500 ) {
			log.clear();
		}
		log.add( rd );
	}

}
