package cmabreu.sagitarii.metrics;

import java.util.ArrayList;
import java.util.List;

public class MetricController {
	private static MetricController instance;
	private List<MetricEntity> entities; 

	public MetricEntity addEntity( String name, MetricType type ) {
		MetricEntity entity = new MetricEntity( name, type );
		entities.add( entity );
		return entity;
	}
	
	public void reset() {
		entities = new ArrayList<MetricEntity>();
	}
	
	public List<MetricEntity> getEntities() {
		return new ArrayList<MetricEntity>( entities );
	}
	
	public static MetricController getInstance() {
		if( instance == null ) {
			instance = new MetricController();
		}
		return instance;
	}
	
	private MetricController() {
		entities = new ArrayList<MetricEntity>();
	}
	
	public void computeMetrics() {
		for ( MetricEntity entity : getEntities() ) {
			entity.calcHitsPerSecond();
		}
		
	}

	public MetricEntity getEntity( String name ) {
		for ( MetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				return entity;
			}
		}
		return null;
	}

	public void setTimeSpent( String name, double time ) {
		for ( MetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				entity.setTimeSpent(time);	
			}
		}
	}

	public synchronized void hit( String name, MetricType type ) {
		boolean found = false;
		for ( MetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				entity.hit();
				found = true;
				break;
			}
		}
		if ( !found ) {
			addEntity(name,type).hit();
		}
	}
	
}
