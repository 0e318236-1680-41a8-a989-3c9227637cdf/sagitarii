package cmabreu.sagitarii.core;

import cmabreu.sagitarii.core.delivery.InstanceDeliveryControl;


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
    	InstanceDeliveryControl.getInstance().checkLostPackets();
    }
	

}
