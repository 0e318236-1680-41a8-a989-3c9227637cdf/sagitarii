package cmabreu.sagitarii.core;

import cmabreu.sagitarii.core.statistics.AgeCalculator;


/**
 * Main system heartbeat
 * 
 * @author contmagno
 *
 */
public class ActivationScheduler implements Runnable {
	
    @Override
    public void run() {
    	ClustersManager.getInstance().updateClustersStatus();
    	Sagitarii.getInstance().loadInputBuffer();
    	AgeCalculator.getInstance().compute();
    }
	

}
