package cmabreu.sagitarii.metrics;

import cmabreu.sagitarii.core.config.Configurator;

public class Chronos implements Runnable  {

	@Override
	public void run() {
		
		try {
			long freeMemory = Configurator.getInstance().getFreeMemory() / 1048576;
			long totalMemory = Configurator.getInstance().getTotalMemory() / 1048576;
			double cpuLoad = Configurator.getInstance().getProcessCpuLoad();
			double percent = Math.round( (freeMemory * 100 ) / totalMemory );

			MetricController.getInstance().set( percent, "Memory", MetricType.LOAD );
			MetricController.getInstance().set( cpuLoad, "CPU", MetricType.LOAD );
			
		} catch ( Exception e ) {  }	
		
		MetricController.getInstance().computeMetrics();
	}
	
	
}
