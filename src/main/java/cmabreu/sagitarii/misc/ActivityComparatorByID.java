package cmabreu.sagitarii.misc;

import java.util.Comparator;

import cmabreu.sagitarii.persistence.entity.Activity;

public class ActivityComparatorByID implements Comparator<Activity>  {

	@Override
	public int compare(Activity arg0, Activity arg1) {
    	if (arg0 == null || arg1 == null) { return 0; }
		if ( arg0.getIdActivity() == arg1.getIdActivity() ) { return 0; }
		
        if (arg0.getIdActivity() < arg1.getIdActivity()) {
            return -1;
        }
        if (arg0.getIdActivity() > arg1.getIdActivity()) {
            return 1;
        }		
		return 0;
	}

}
