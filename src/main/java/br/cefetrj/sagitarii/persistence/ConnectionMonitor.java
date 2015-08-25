package br.cefetrj.sagitarii.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionMonitor {
	private static ConnectionMonitor instance;
	private Map<String,Integer> connections;
	private Logger logger;

	public static ConnectionMonitor getinstance() {
		if ( instance == null ) {
			instance = new ConnectionMonitor();
		}
		return instance;
	}
	
	public ConnectionMonitor() {
		connections = new HashMap<String,Integer>();
		logger = LogManager.getLogger( this.getClass().getName() );
	}
	
	public synchronized void releaseMonitor( String className ) {
		logger.debug("release monitor for entity " + className);
		Integer quant = connections.get(className);
		if ( quant != null ) {
			quant--;
			logger.debug("connection for entity " + className + " released: " + quant + " connections left open");
			connections.put(className, quant);
		}
	}
	
	public synchronized void startMonitor( String className ) {
		logger.debug("start monitor for entity " + className);
		Integer quant = connections.get(className);
		if ( (quant != null) && ( quant > 0 ) ) {
			logger.warn("too much connections for entity " + className);
		} else {
			quant = 0;
		}
		quant++;
		logger.debug( quant + " active connections for "  + className );
		connections.put(className, quant);
	}
	
	public void printMap() {
	    Iterator<Entry<String, Integer>> it = connections.entrySet().iterator();
	    while ( it.hasNext() ) {
	        Map.Entry<String,Integer> pair = it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); 
	    }
	}	
	
}
