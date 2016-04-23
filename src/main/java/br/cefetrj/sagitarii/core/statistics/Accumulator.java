package br.cefetrj.sagitarii.core.statistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.persistence.entity.TimeControl;

public class Accumulator {
	private Long averageMillis = 0L;
	private int calculatedCount = 0;
	private String hash;
	private Long totalAgeMillis = 0L;
	private Long maxAgeMillis = 0L;
	private Long minAgeMillis = 0L;
	private int idTimeControl = -1;
	private String content;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public int getIdTimeControl() {
		return idTimeControl;
	}
	
	public void setIdTimeControl(int idTimeControl) {
		this.idTimeControl = idTimeControl;
	}

	public Accumulator( DeliveryUnit du  ) {
		this.hash = du.getHash();
		this.content = du.getInstanceActivities();
		addToStack( du );
		logger.debug("[AC] new Accumulator for " + content + ": " + averageMillis + " | " + totalAgeMillis + " | " + calculatedCount );
	}

	public Accumulator( TimeControl tc  ) {
		this.averageMillis = tc.getAverageMilis();
		this.calculatedCount = tc.getCalculatedCount();
		this.hash = tc.getHash();
		this.idTimeControl = tc.getIdTimeControl();
		this.totalAgeMillis = tc.getTotalAgeMilis();
		this.content = tc.getContent();
		logger.debug("[DB] new Accumulator for " + content + ": " + averageMillis + " | " + totalAgeMillis + " | " + calculatedCount );
	}
	
	public String getContent() {
		return content;
	}
	
	public long getAverageMillis() {
		return averageMillis;
	}
	
	public void addToStack( DeliveryUnit du ) {
		Long duAgeMillis = du.getAgeMillis();
		if ( duAgeMillis > maxAgeMillis ) {
			maxAgeMillis = duAgeMillis;
		}
		if ( duAgeMillis < minAgeMillis) {
			minAgeMillis = duAgeMillis;
		}
		logger.debug("updating average count for " + content + ": " + averageMillis + " | " + totalAgeMillis + " | " + calculatedCount + " | " + duAgeMillis );
		calculatedCount++;
		Long newTotalAgeMillis = totalAgeMillis + duAgeMillis;
		logger.debug(newTotalAgeMillis + " = " + totalAgeMillis + " + " +duAgeMillis);
		averageMillis = newTotalAgeMillis / calculatedCount;
		logger.debug("new average count for " + content + ": " + averageMillis + " = " + newTotalAgeMillis + " / " + calculatedCount );
		totalAgeMillis = newTotalAgeMillis;
	}
	
	public int getCalculatedCount() {
		return calculatedCount;
	}

	public String getAverageAgeAsText() {
		return DateLibrary.getInstance().getTimeRepresentation(averageMillis);
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public long getTotalAgeMillis() {
		return totalAgeMillis;
	}
	
}
