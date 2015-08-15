package br.cefetrj.sagitarii.metrics;

import java.util.ArrayList;
import java.util.List;

public class MetricController {
	private static MetricController instance;
	private List<IMetricEntity> entities; 

	public IMetricEntity addHitEntity( String name, MetricType type ) {
		IMetricEntity entity = new MetricHitEntity( name, type );
		entities.add( entity );
		return entity;
	}

	public IMetricEntity addValueEntity( String name, MetricType type ) {
		IMetricEntity entity = new MetricValueEntity( name, type );
		entities.add( entity );
		return entity;
	}

	public void reset() {
		entities = new ArrayList<IMetricEntity>();
	}
	
	public List<IMetricEntity> getEntities() {
		return new ArrayList<IMetricEntity>( entities );
	}
	
	public static MetricController getInstance() {
		if( instance == null ) {
			instance = new MetricController();
		}
		return instance;
	}
	
	private MetricController() {
		entities = new ArrayList<IMetricEntity>();
	}
	
	public void computeMetrics() {
		for ( IMetricEntity entity : getEntities() ) {
			entity.calc();
		}
		
	}

	public IMetricEntity getEntity( String name ) {
		for ( IMetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				return entity;
			}
		}
		return null;
	}

	public void setTimeSpent( String name, double time ) {
		for ( IMetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				entity.setTimeSpent(time);	
			}
		}
	}

	public synchronized void hit( String name, MetricType type ) {
		boolean found = false;
		for ( IMetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				( (MetricHitEntity)entity ).hit();
				found = true;
				break;
			}
		}
		if ( !found ) {
			IMetricEntity entity = addHitEntity(name,type);
			( (MetricHitEntity)entity ).hit();
		}
	}

	
	public synchronized void set(double value, String name, MetricType type ) {
		boolean found = false;
		for ( IMetricEntity entity : getEntities() ) {
			if ( entity.getName().equals( name ) ) {
				( (MetricValueEntity)entity ).set(value);
				found = true;
				break;
			}
		}
		if ( !found ) {
			IMetricEntity entity = addValueEntity(name,type);
			( (MetricValueEntity)entity ).set(value);
		}
	}

	
}
