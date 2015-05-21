package cmabreu.sagitarii.core.delivery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cmabreu.sagitarii.core.statistics.AgeCalculator;
import cmabreu.sagitarii.persistence.entity.Pipeline;

public class InstanceDeliveryControl {
	private List<DeliveryUnit> units;
	private static InstanceDeliveryControl instance;
	
	public static InstanceDeliveryControl getInstance() {
		if ( instance == null ) {
			instance = new InstanceDeliveryControl();
		}
		return instance;
	}
	
	public synchronized List<DeliveryUnit> getUnits() {
		return new ArrayList<DeliveryUnit>( units );
	}
	
	private InstanceDeliveryControl() {
		units = new ArrayList<DeliveryUnit>();
	}
	
	public synchronized void addUnit( Pipeline pipeline, String macAddress ) {
		DeliveryUnit du = new DeliveryUnit();
		du.setMacAddress(macAddress);
		du.setPipeline(pipeline);
		du.setDeliverTime( Calendar.getInstance().getTime() );
		units.add(du);
	}

	
	public void removeUnit( String pipelineSerial ) {
		for ( DeliveryUnit du : units ) {
			if ( du.getPipeline().getSerial().equalsIgnoreCase( pipelineSerial ) ) {
				units.remove( du );
				du.setReceiveTime( Calendar.getInstance().getTime() );
				AgeCalculator.getInstance().addToStatistics( du );
				break;
			}
		}
	}
	
	
}
