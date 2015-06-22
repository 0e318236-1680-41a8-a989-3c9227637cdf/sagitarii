package cmabreu.sagitarii.core.statistics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import cmabreu.sagitarii.core.delivery.DeliveryUnit;
import cmabreu.sagitarii.misc.DateLibrary;

public class Accumulator {
	private Date averageAge;
	private long averageMilis = 0;
	private int calculatedCount;
	private String hash;
	private long totalAgeMilis;
	
	public Accumulator( DeliveryUnit du  ) {
		this.hash = du.getHash();
		addToStack( du );
	}
	
	public long getAverageMilis() {
		return averageMilis;
	}
	
	public void addToStack( DeliveryUnit du ) {
		calculatedCount++;
		totalAgeMilis = totalAgeMilis + du.getAgeMillis();
		long averageMilis = totalAgeMilis / calculatedCount;
		
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
