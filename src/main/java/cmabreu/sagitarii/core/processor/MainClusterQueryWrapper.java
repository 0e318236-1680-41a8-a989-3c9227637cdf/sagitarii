package cmabreu.sagitarii.core.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.services.RelationService;

public class MainClusterQueryWrapper {
	private	Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public void executeQuery( Activation act ) throws Exception {
		String sql = act.getCommand();
		
		//act.getTargetTable()
		
		/*
		 * 
		 * 
		 * COPY (SELECT * FROM insurance) TO 'd:\myfile1.csv' With csv;
		 * COPY insurance_out from 'd:\myfile1.csv'  With csv;
		 * 
		 * 
		 */
		
		logger.debug( sql );
		
		RelationService rs = new RelationService();
		rs.executeQuery( sql );
		
	}

}
