package cmabreu.sagitarii.core;

import cmabreu.sagitarii.core.types.ActivityType;
import cmabreu.sagitarii.persistence.entity.Activity;

public class ActivityFactory {
	
	public static Activity getActivity( ActivityType type ) {
		Activity act = new Activity( );
		act.setType(type);
		return act;
	}

}
