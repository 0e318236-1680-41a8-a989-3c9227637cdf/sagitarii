package cmabreu.sagitarii.core.statistics;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import cmabreu.sagitarii.core.delivery.DeliveryUnit;
import cmabreu.sagitarii.misc.DateLibrary;

public class Accumulator {
	private Date averageAge;
	private int calculatedCount;
	private String executorAlias;
	private String executorType;
	private long totalAgeMilis;
	
	public Accumulator( DeliveryUnit du  ) {
		executorAlias = du.getInstance().getExecutorAlias();
		executorType = du.getInstance().getType().toString();
		addToStack( du );
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

	public String getAlias() {
		return executorAlias;
	}
	
	public Date getAverageAge() {
		return averageAge;
	}
	
	public long getTotalAgeMilis() {
		return totalAgeMilis;
	}

	public String getType() {
		return executorType;
	}
	
}
