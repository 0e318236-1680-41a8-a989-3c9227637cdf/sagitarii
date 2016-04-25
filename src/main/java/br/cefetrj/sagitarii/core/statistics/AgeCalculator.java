package br.cefetrj.sagitarii.core.statistics;

import java.util.ArrayList;
import java.util.List;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.persistence.entity.TimeControl;
import br.cefetrj.sagitarii.persistence.services.TimeControlService;

public class AgeCalculator {
	private List<Accumulator> list;
	private static AgeCalculator instance;
	private int totalAdded = 0;
	
	public void storeList() {
		try {
			for ( Accumulator ac : list ) {
				TimeControl tc = new TimeControl(ac.getIdTimeControl(), ac.getAverageMillis(), ac.getCalculatedCount(),	
						ac.getHash(), ac.getTotalAgeMillis(), ac.getContent() );
				
				TimeControlService tcs = new TimeControlService();
				if ( ac.getIdTimeControl() == -1 ) {
					// New ACC
					TimeControl newTc = tcs.insertTimeControl(tc);
					ac.setIdTimeControl( newTc.getIdTimeControl() );
				} else {
					// Update
					tcs.updateTimeControl(tc);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void retrieveList() throws Exception {
		TimeControlService tcs = new TimeControlService();
		list = tcs.getList();
	}
	
	private AgeCalculator() {
		list = new ArrayList<Accumulator>();
	}
	
	public static AgeCalculator getInstance() {
		if( instance == null ) {
			instance = new AgeCalculator();
		}
		return instance;
	}
	
	public List<Accumulator> getList() {
		return new ArrayList<Accumulator>(list);
	}
	

	public Accumulator getAccumulator( String hash ) {
		for ( Accumulator accumulator : list  ) {
			if( accumulator.getHash().equals(hash)  ) {
				return accumulator;
			}
		}
		return null;
	}

	public void addToStatistics( DeliveryUnit du ) {
		boolean found = false;
		totalAdded++;
		for ( Accumulator accumulator : list  ) {
			if( accumulator.getHash().equals( du.getHash() )  ) {
				accumulator.addToStack( du );
				found = true;
			}
		}
		if ( !found ) {
			list.add ( new Accumulator( du ) );
		}
		
		// Every 10 updates, store / update list on database
		if ( totalAdded == 10 ) {
			totalAdded = 0;
			storeList();
		}
		
		
	}
	
}
