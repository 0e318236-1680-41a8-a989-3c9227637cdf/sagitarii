package br.cefetrj.sagitarii.core.statistics;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.persistence.entity.TimeControl;

public class Accumulator {
	private long averageMillis = 0;
	private int calculatedCount = 0;
	private String hash;
	private long totalAgeMillis = 0;
	private int idTimeControl = -1;
	private String content;
	
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
	}

	public Accumulator( TimeControl tc  ) {
		this.averageMillis = tc.getAverageMilis();
		this.calculatedCount = tc.getCalculatedCount();
		this.hash = tc.getHash();
		this.idTimeControl = tc.getIdTimeControl();
		this.totalAgeMillis = tc.getTotalAgeMilis();
		this.content = tc.getContent();
	}
	
	public String getContent() {
		return content;
	}
	
	public long getAverageMillis() {
		return averageMillis;
	}
	
	public void addToStack( DeliveryUnit du ) {
		calculatedCount++;
		totalAgeMillis = totalAgeMillis + du.getAgeMillis();
		averageMillis = totalAgeMillis / calculatedCount;
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
