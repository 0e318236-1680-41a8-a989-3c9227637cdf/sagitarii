package br.cefetrj.sagitarii.core.processor;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.persistence.services.RelationService;

public class MainClusterQueryWrapper {
	private	Logger logger = LogManager.getLogger( this.getClass().getName() );
	private long startTime;
	private long finishTime;
	
	public long getExecutionTimeMillis() {
		return finishTime - startTime;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getFinishTime() {
		return finishTime;
	}
	
	public void executeQuery( Activation act ) throws Exception {
		startTime = Calendar.getInstance().getTimeInMillis();
		String sql = act.getCommand();
		logger.debug( sql );
		RelationService rs = new RelationService();
		rs.executeQuery( sql );
		finishTime = Calendar.getInstance().getTimeInMillis();
	}

}
