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

	/*
	private boolean mustInform( Accumulator ac, DeliveryUnit unit ) {

		// Below this limit all is normal
		if ( !isTakingTooMuchTime(unit)  ) {
			return false;
		}
		
		long m1 = unit.getAgeMillis();
		long m2 = ac.getAverageMillis();
		long mt = ac.getTotalAgeMillis();
		
		long secs = ( (m1 - m2) / 1000 ) + 1 ;
		long secsLimit = ( m2  / 1000 );
		
		logger.debug(" > Hash: " + ac.getHash() );
		logger.debug("   > Unity Age  : " + DateLibrary.getInstance().getTimeRepresentation( m1 ) );
		logger.debug("   > Average    : " + DateLibrary.getInstance().getTimeRepresentation( m2 ) );
		logger.debug("   > Limit      : " + secsLimit );
		logger.debug("   > Total      : " + DateLibrary.getInstance().getTimeRepresentation( mt ) );
		logger.debug("   > Diff Secs. : " + secs );
		
		if ( secs > secsLimit ) { // Is taking more than double of average time for this kind of instance
			logger.debug( ac.getHash() + " must inform");
			return true;
		} else {
			logger.debug( ac.getHash() + " its ok");
			return false;
		}
	}

	private boolean isDelayed( Accumulator ac, DeliveryUnit unit ) {
		long m1 = unit.getAgeMillis();
		long m2 = ac.getAverageMillis();
		long secs = ( (m1 - m2) / 1000 ) + 1 ;
		
		if ( secs > 0 ) { // Is taking more than the average time for this kind of instance
			return true;
		} else {
			return false;
		}
	}

	private boolean isTakingTooMuchTime( DeliveryUnit unit ) {
		try {
			long m1 = unit.getAgeMillis();
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
		for ( DeliveryUnit unit : getUnits() ) {
			// We take the Average time for this kind of Instance
			// by checking the accumulator with same hash
			Accumulator ac = AgeCalculator.getInstance().getAccumulator( unit.getHash() );
			if ( ac != null ) {
				if ( isDelayed( ac, unit ) ) {
					unit.setDelayed();
				}
				if ( mustInform( ac, unit ) ) {
					ClustersManager.getInstance().inform( unit.getMacAddress(), unit.getInstance().getSerial(), false );
				}
			} else {
				if ( isTakingTooMuchTime( unit ) ) {
					logger.debug( "instance '" + unit.getInstanceActivities() + "' is taking too much time than the system limit");
					ClustersManager.getInstance().inform( unit.getMacAddress(), unit.getInstance().getSerial(), false );
				}
			}
		}
	}

	public void forceInformAllDelayed() {
		for ( DeliveryUnit unit : getUnits() ) {
			Accumulator ac = AgeCalculator.getInstance().getAccumulator( unit.getHash() );
			if ( ac != null ) {
				if ( isDelayed( ac, unit ) ) {
					ClustersManager.getInstance().inform( unit.getMacAddress(), unit.getInstance().getSerial(), true );
				}
			}
		}
	}
	
	public void forceInformDelayed( String instance ) {
		for ( DeliveryUnit unit : getUnits() ) {
			if ( instance.equals( unit.getInstance().getSerial() ) ) {
				logger.debug("force asking for Instance " + instance );
				ClustersManager.getInstance().inform( unit.getMacAddress(), unit.getInstance().getSerial(), true );
			}
		}
	}
	*/
	
	// The node is idle but have an instance registered in IDC to it ( probably lost ). Try to run the instance again.
	public void claimInstance( Node node ) {
		for ( DeliveryUnit unit : getUnits() ) {
			if ( unit.isDelayed() &&  node.getmacAddress().equals( unit.getMacAddress() ) ) {
				logger.debug("instance " + unit.getInstance().getSerial() + " was lost and claimed by idle node " + node.getmacAddress() );
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
		//du.setDeliverTime( Calendar.getInstance().getTime() );
		units.add(du);
	}

	public void cancelUnit( String instanceSerial ) {
		for ( DeliveryUnit du : units ) {
			if ( du.getInstance().getSerial().equalsIgnoreCase( instanceSerial ) ) {
				logger.debug("will cancel Instance " + instanceSerial + " from Delivery Control");
				units.remove( du );
				break;
			}
		}
	}
	
	public void removeUnit( String instanceSerial ) {
		for ( DeliveryUnit du : units ) {
			if ( du.getInstance().getSerial().equalsIgnoreCase( instanceSerial ) ) {
				logger.debug("will remove Instance " + instanceSerial + " from Delivery Control and add it to statistics");
				units.remove( du );
				//du.setReceiveTime( Calendar.getInstance().getTime() );
				AgeCalculator.getInstance().addToStatistics( du );
				break;
			}
		}
	}
	
	
}
