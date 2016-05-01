package br.cefetrj.sagitarii.core.delivery;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.Node;
import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.statistics.AgeCalculator;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.persistence.entity.Instance;

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
	
	public String getFirstDelayLimitSeconds() {
		try {
			long millis = Configurator.getInstance().getFirstDelayLimitSeconds() * 1000;
			return DateLibrary.getInstance().getTimeRepresentation(millis);
		} catch ( Exception e ) {
			return "CONFIG_ERROR";
		}
	}

	public void claimInstance( Node node ) {
		for ( DeliveryUnit unit : getUnits() ) {
			if ( node.getmacAddress().equals( unit.getMacAddress() ) ) {
				logger.error("instance " + unit.getInstance().getSerial() + " was lost and claimed by idle node " + node.getmacAddress() );
				node.resubmitInstanceToBuffer( unit.getInstance().getSerial() );
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
		units.add(du);
	}

	public synchronized void cancelUnit( String instanceSerial ) {
		for ( DeliveryUnit du : getUnits() ) {
			if ( du.getInstance().getSerial().equalsIgnoreCase( instanceSerial ) ) {
				logger.debug("will cancel Instance " + instanceSerial + " from Delivery Control");
				removeFromList(du);
				break;
			}
		}
	}
	
	public synchronized void removeUnit( String instanceSerial ) {
		for ( DeliveryUnit du : getUnits() ) {
			if ( du.getInstance().getSerial().equalsIgnoreCase( instanceSerial ) ) {
				logger.debug("will remove Instance " + instanceSerial + " from Delivery Control and add it to statistics");
				removeFromList(du);
				AgeCalculator.getInstance().addToStatistics( du );
				break;
			}
		}
	}
	
	private void removeFromList( DeliveryUnit du ) {
		try {
			units.remove( du );
		} catch ( Exception e ) {
			logger.error("error removing Delivery Unit from list: " + du.getMacAddress() + " | " + du.getInstance().getSerial() );
			logger.error(" > " + e.getMessage() );
		}
	}
	
}
