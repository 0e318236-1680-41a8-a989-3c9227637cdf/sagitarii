package br.cefetrj.sagitarii.core.statistics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.persistence.entity.TimeControl;

public class Accumulator {
	private Date averageAge;
	private long averageMilis = 0;
	private int calculatedCount = 0;
	private String hash;
	private long totalAgeMilis = 0;
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
		this.averageAge = tc.getAverageAge();
		this.averageMilis = tc.getAverageMilis();
		this.calculatedCount = tc.getCalculatedCount();
		this.hash = tc.getHash();
		this.idTimeControl = tc.getIdTimeControl();
		this.totalAgeMilis = tc.getTotalAgeMilis();
		this.content = tc.getContent();
	}
	
	public String getContent() {
		return content;
	}
	
	public long getAverageMilis() {
		return averageMilis;
	}
	
	public void addToStack( DeliveryUnit du ) {
		calculatedCount++;
		totalAgeMilis = totalAgeMilis + du.getAgeMillis();
		averageMilis = totalAgeMilis / calculatedCount;
		
		String time = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(averageMilis),
				TimeUnit.MILLISECONDS.toMinutes(averageMilis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(averageMilis)), 
				TimeUnit.MILLISECONDS.toSeconds(averageMilis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(averageMilis)));
		
		DateLibrary.getInstance().setTimeTextHuman( time );
	    averageAge = DateLibrary.getInstance().asDate();
	}
	
	public int getCalculatedCount() {
		return calculatedCount;
	}

	public String getAverageAgeAsText() {
		averageMilis = totalAgeMilis / calculatedCount;
		String time = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(averageMilis),
				TimeUnit.MILLISECONDS.toMinutes(averageMilis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(averageMilis)), 
				TimeUnit.MILLISECONDS.toSeconds(averageMilis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(averageMilis)));
		return time;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public Date getAverageAge() {
		return averageAge;
	}
	
	public long getTotalAgeMilis() {
		return totalAgeMilis;
	}
	
}
