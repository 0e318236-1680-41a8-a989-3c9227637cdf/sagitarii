package br.cefetrj.sagitarii.metrics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.core.types.ExperimentStatus;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.services.RelationService;

public class ExperimentPerformance {
	private Experiment experiment;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public ExperimentPerformance( Experiment experiment ) {
		this.experiment = experiment;
	}
	
	public ExperimentPerformance() {
		// Public default
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public double getSpeedUp() {
		logger.debug("get speedup for experiment " + experiment.getTagExec() + "...");
		Double speedUp = 0.0;
		try {
			long parallelTime = getElapsedMillis();
			long sequencialTime = getSerialTimeMillis();
			speedUp = (double)sequencialTime / (double)parallelTime;
		} catch ( Exception  e) {
			logger.error( e.getMessage() );
		}
		if ( speedUp.isNaN() ) {
			logger.error("speedup is NaN");
			speedUp = 0.0;
		} else {
			logger.debug("done: speedup is " + speedUp);
		}
		return speedUp;
	}
	
	
	private long getSerialTimeMillis() {
		logger.debug("get serial time (ms) for experiment " + experiment.getTagExec() );
		int qtd = 0;
		try {
			RelationService rs = new RelationService();
			Set<UserTableEntity> result = rs.genericFetchList("select sum(elapsed_millis) as sum from "
					+ "instances where id_fragment in ( select id_fragment from fragments where id_experiment = "+
					experiment.getIdExperiment()+" )");
			
			List<UserTableEntity> res = new ArrayList<UserTableEntity> ( result );
			if ( res.size() > 0 ) {
				UserTableEntity ute = res.get(0);
				String sQtd = ute.getData("sum");
				qtd = Integer.valueOf( sQtd );
			}
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
		logger.debug("done: " + qtd + "ms");
		return qtd;
	}
	
	public String getRealTime() {
		return DateLibrary.getInstance().getTimeRepresentation( getRealTimeMillis() );
	}
	
	public String getLazyTime() {
		return DateLibrary.getInstance().getTimeRepresentation( getElapsedMillis() - getRealTimeMillis() );
	}
	
	public double getParallelEfficiency() {
		logger.debug("get parallel efficiency for experiment " + experiment.getTagExec() );
		int coresWorking = experiment.getCoresWorking();
		Double parallelEfficiency = 0.0;
		Double speedUp = getSpeedUp();
		
		logger.debug("cores: " + coresWorking + " speedup: " + speedUp );

		if ( coresWorking == 0 ) return 0.0;
		if ( speedUp == 0.0 ) {
			parallelEfficiency = speedUp;
		} else {
			try {
				parallelEfficiency = speedUp / coresWorking;
			} catch ( Exception e ) { 
				logger.error( e.getMessage() );
			}
		}
		if ( parallelEfficiency.isNaN() ) {
			logger.error("parallel efficiency is NaN");
			parallelEfficiency = 0.0;
		} else {
			logger.debug("done: " + parallelEfficiency );
		}
		return parallelEfficiency;
	}	

	private long getRealTimeMillis() {
		logger.debug("get real time (ms) for experiment " + experiment.getTagExec() );
		int qtd = 0;
		try {
			RelationService rs = new RelationService();
			Set<UserTableEntity> result = rs.genericFetchList("select sum( real_finish_time_millis - real_start_time_millis ) as sum from "
					+ "instances where id_fragment in ( select id_fragment from fragments where id_experiment = "+
					experiment.getIdExperiment()+" )");
			
			List<UserTableEntity> res = new ArrayList<UserTableEntity> ( result );
			if ( res.size() > 0 ) {
				UserTableEntity ute = res.get(0);
				String sQtd = ute.getData("sum");
				qtd = Integer.valueOf( sQtd );
			}
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
		logger.debug("done: " + qtd + "ms");
		return qtd;
	}
	
	public String getElapsedTime() {
		return DateLibrary.getInstance().getTimeRepresentation( getElapsedMillis() );
	}
	
	public long getElapsedMillis() {
		logger.debug("get elapsed time (ms) for experiment " + experiment.getTagExec() );
		
		DateLibrary dl = DateLibrary.getInstance();
		dl.setTo( experiment.getLastExecutionDate() );
		Calendar cl = Calendar.getInstance();
		
		if ( experiment.getFinishDateTime() != null ) {
			cl.setTime( experiment.getFinishDateTime() );
		} else {
			cl.setTime( Calendar.getInstance().getTime() );
		}
		
		long millis = dl.getDiffMillisTo( cl ) ;

		if ( experiment.getStatus() == ExperimentStatus.STOPPED ) {
			logger.debug("experiment is STOPPED. cannot get time");
			millis = 0;
		}
		logger.debug("done: " + millis + "ms");
		return millis;
	}	
	
	public String getSerialTime() {
		return DateLibrary.getInstance().getTimeRepresentation( getSerialTimeMillis() );
	}
	
	
}
