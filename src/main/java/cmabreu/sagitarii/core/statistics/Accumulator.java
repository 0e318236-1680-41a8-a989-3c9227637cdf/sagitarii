package cmabreu.sagitarii.core.statistics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import cmabreu.sagitarii.core.delivery.DeliveryUnit;
import cmabreu.sagitarii.misc.DateLibrary;

public class Accumulator {
	private Date averageAge;
	private long averageMilis = 0;
	private int calculatedCount = 0;
	private String hash;
	private long totalAgeMilis = 0;
	private String content;
	
	public Accumulator( DeliveryUnit du  ) {
		this.hash = du.getHash();
		this.content = du.getInstanceActivities();
		addToStack( du );
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
