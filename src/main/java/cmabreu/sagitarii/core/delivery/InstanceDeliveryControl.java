package cmabreu.sagitarii.core.delivery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.core.config.Configurator;
import cmabreu.sagitarii.core.statistics.Accumulator;
import cmabreu.sagitarii.core.statistics.AgeCalculator;
import cmabreu.sagitarii.misc.DateLibrary;
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
	
	public String getFirstDelayLimitSeconds() {
		try {
			long millis = Configurator.getInstance().getFirstDelayLimitSeconds() * 1000;
			String retorno = String.format("%02d:%02d:%02d", 
					TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis) -  
					TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
					TimeUnit.MILLISECONDS.toSeconds(millis) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
			return retorno;
		} catch ( Exception e ) {
			return "CONFIG_ERROR";
		}
	}

	
	/**
	 * Check if Sagitarii must ask the node for an instance that is take much more time
	 * than the average
	 */
	private boolean mustInform( Accumulator ac, DeliveryUnit unity ) {

		long m1 = unity.getAgeMillis();
		long m2 = ac.getAverageMilis();
		long mt = ac.getTotalAgeMilis();
		
		long secs = ( (m1 - m2) / 1000 ) + 1 ;
		long secsLimit = ( m2  / 1000 );
		
		logger.debug(" > Hash: " + ac.getHash() );
		logger.debug("   > Unity Age  : " + DateLibrary.getInstance().getTimeRepresentation( m1 ) );
		logger.debug("   > Average    : " + DateLibrary.getInstance().getTimeRepresentation( m2 ) );
		logger.debug("   > Limit      : " + secsLimit );
		logger.debug("   > Total      : " + DateLibrary.getInstance().getTimeRepresentation( mt ) );
		logger.debug("   > Diff Secs. : " + secs );
		
		if ( secs > secsLimit ) { // Is taking more than double of average time for this kind of instance
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Check if an instance that is taking many time as the average
	 */
	private boolean isDelayed( Accumulator ac, DeliveryUnit unity ) {
		long m1 = unity.getAgeMillis();
		long m2 = ac.getAverageMilis();
		long secs = ( (m1 - m2) / 1000 ) + 1 ;
		
		if ( secs > 0 ) { // Is taking more than the average time for this kind of instance
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if an instance that is taking many time as we think is too much
	 * For all first run is a time limit. After first run, the average time is taken. 
	 */
	private boolean isTakingTooMuchTime( DeliveryUnit unity ) {
		try {
			long m1 = unity.getAgeMillis();
			long m2 = Configurator.getInstance().getFirstDelayLimitSeconds();
			long secs = ( ( m1 / 1000 ) - m2 + 1 ) ;
			
			if ( secs > 0 ) { // Is taking too much.
				return true;
			} else {
				return false;
			}
		} catch ( Exception e ) {
			return false;
		}
	}
	
	
	public void checkLostPackets() {
		// For each Instance we have with nodes...
		for ( DeliveryUnit unity : getUnits() ) {
			// We take the Average time for this kind of Instance
			// by checking the accumulator with same hash
			Accumulator ac = AgeCalculator.getInstance().getAccumulator( unity.getHash() );
			if ( ac != null ) {
				if ( isDelayed( ac, unity ) ) {
					unity.setDelayed();
				}
				if ( mustInform( ac, unity ) ) {
					ClustersManager.getInstance().inform( unity.getMacAddress(), unity.getInstance().getSerial() );
				}
			} else {
				if ( isTakingTooMuchTime( unity ) ) {
					logger.debug( "instance '" + unity.getInstanceActivities() + "' is taking too much time than the system limit");
					ClustersManager.getInstance().inform( unity.getMacAddress(), unity.getInstance().getSerial() );
				}
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
