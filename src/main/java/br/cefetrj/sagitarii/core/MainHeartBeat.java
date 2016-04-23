package br.cefetrj.sagitarii.core;

import br.cefetrj.sagitarii.metrics.MetricController;


/**
 * Main system heartbeat
 * @author Carlos Magno Abreu
 *
 */
public class MainHeartBeat implements Runnable {
	
    @Override
    public void run() {
    	try {
	    	NodesManager.getInstance().updateNodesStatus();
	    	Sagitarii.getInstance().loadInputBuffer();
	    	
	   		// InstanceDeliveryControl.getInstance().checkLostPackets();
	   		
	   		NodesManager.getInstance().saveMetrics();
	   		MetricController.getInstance().saveMetrics();
    	} catch ( Exception e ) {
    		// I'll NEVER stop !
    	}
    }
	

}
