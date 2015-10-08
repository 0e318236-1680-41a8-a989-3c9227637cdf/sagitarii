package br.cefetrj.parser;

import java.util.HashMap;
import java.util.Map;

public class ActivityList {
	private Map<Integer, Activity> activities;
	
	public void showList() {
		for (Map.Entry<Integer, Activity> entry : activities.entrySet())	{
		    System.out.println(entry.getKey() + " | " + entry.getValue().getName() );
			for ( Activity act : entry.getValue().getPreviousActivities() ) {
				System.out.println( " > " + act.getName() );
			}
		    
		}
	}
	
	
	public void addAsPrevious( int parentReference, Activity previous ) {
		getActivity(parentReference).getPreviousActivities().add( previous );
	}
	
	public Activity getActivity( int reference ) {
		return activities.get( reference );
	}
	
	public Activity startActivity( int reference ) {
		Activity activity = new Activity();
		activities.put(reference, activity);
		return activity;
	}

	public void putActivity( int reference, Activity activity ) {
		activities.put(reference, activity);
	}
	
	public ActivityList() {
		activities = new HashMap<Integer,Activity>();
	}
	
}
