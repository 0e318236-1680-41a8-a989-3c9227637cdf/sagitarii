package br.cefetrj.sagitarii.core.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.persistence.services.RelationService;

public class MainClusterQueryWrapper {
	private	Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public void executeQuery( Activation act ) throws Exception {
		String sql = act.getCommand();
		
		logger.debug( sql );
		
		RelationService rs = new RelationService();
		rs.executeQuery( sql );
		
	}

}
