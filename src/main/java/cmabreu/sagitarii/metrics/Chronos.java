package cmabreu.sagitarii.metrics;

public class Chronos implements Runnable  {

	@Override
	public void run() {
		MetricController.getInstance().computeMetrics();
	}
	
	
}
