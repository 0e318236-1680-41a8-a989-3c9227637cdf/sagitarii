package cmabreu.sagitarii.core;

import cmabreu.sagitarii.core.delivery.InstanceDeliveryControl;


/**
 * Main system heartbeat
 * @author Carlos Magno Abreu
 *
 */
public class MainHeartBeat implements Runnable {
	
    @Override
    public void run() {
    	ClustersManager.getInstance().updateClustersStatus();
    	Sagitarii.getInstance().loadInputBuffer();
   		InstanceDeliveryControl.getInstance().checkLostPackets();
    }
	

}
