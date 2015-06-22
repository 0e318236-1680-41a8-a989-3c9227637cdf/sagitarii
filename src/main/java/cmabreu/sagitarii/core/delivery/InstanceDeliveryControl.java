package cmabreu.sagitarii.core.delivery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.core.statistics.Accumulator;
import cmabreu.sagitarii.core.statistics.AgeCalculator;
import cmabreu.sagitarii.persistence.entity.Instance;

public class InstanceDeliveryControl {
	private List<DeliveryUnit> units;
	private static InstanceDeliveryControl instance;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public static InstanceDeliveryControl getInstance() {
		if ( instance == null ) {
			instance = new InstanceDeliveryControl();
		}
		return instance;
	}
	
	public void checkLostPackets() {
		// For each Instance we have with nodes...
		for ( DeliveryUnit unity : units ) {
			// We take the Average time for this kind of Instance
			// by checking the accumulator with same hash
			Accumulator ac = AgeCalculator.getInstance().getAccumulator( unity.getHash() );
			long millis = ac.getTotalAgeMilis() - unity.getAgeMillis();
			logger.debug(" > " + unity.getHash() + ": Time: " + unity.getAgeTime() + " Average: " + ac.getAverageAgeAsText() + " Millis diff:" + millis);
			if ( millis > ( 2 * ac.getAverageMilis() ) ) {
				ClustersManager.getInstance().inform( unity.getMacAddress(), unity.getInstance().getSerial() );
			}
		}
	}
	
	public synchronized List<DeliveryUnit> getUnits() {
		return new ArrayList<DeliveryUnit>( units );
	}
	
	private InstanceDeliveryControl() {
		units = new ArrayList<DeliveryUnit>();
	}
	
	public synchronized void addUnit( Instance instance, String macAddress ) {
		DeliveryUnit du = new DeliveryUnit();
		du.setMacAddress(macAddress);
		du.setInstance(instance);
		du.setDeliverTime( Calendar.getInstance().getTime() );
		units.add(du);
	}

	
	public void removeUnit( String instanceSerial ) {
		for ( DeliveryUnit du : units ) {
			if ( du.getInstance().getSerial().equalsIgnoreCase( instanceSerial ) ) {
				units.remove( du );
				du.setReceiveTime( Calendar.getInstance().getTime() );
				AgeCalculator.getInstance().addToStatistics( du );
				break;
			}
		}
	}
	
	
}
