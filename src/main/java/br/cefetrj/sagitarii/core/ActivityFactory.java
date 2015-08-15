package br.cefetrj.sagitarii.core;

import br.cefetrj.sagitarii.core.types.ActivityType;
import br.cefetrj.sagitarii.persistence.entity.Activity;

public class ActivityFactory {
	
	public static Activity getActivity( ActivityType type ) {
		Activity act = new Activity( );
		act.setType(type);
		return act;
	}

}
