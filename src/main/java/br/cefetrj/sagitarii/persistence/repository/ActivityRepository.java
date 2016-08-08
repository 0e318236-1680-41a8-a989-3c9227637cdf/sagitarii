package br.cefetrj.sagitarii.persistence.repository;

import java.util.List;

import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class ActivityRepository extends BasicRepository {

	public ActivityRepository() throws DatabaseConnectException {
		super();
		logger.debug("repository init");
	}

	public List<Fragment> fillFragments( List<Fragment> fragments) throws NotFoundException {
		logger.debug("retrieve activities list for fragments");
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		List<Activity> activities; 
		try {
			for ( Fragment frag : fragments ) {
				activities = fm.getList("select * from activities where id_fragment = " + frag.getIdFragment() );
				for ( Activity act : activities ) {
					frag.addActivity(act);
				}
			}
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done");
		return fragments;
	}
	
	public List<Activity> getList(int idWorkflow) throws NotFoundException {
		logger.debug("retrieving activities list..." );
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		List<Activity> activities = null;
		try {
			activities = fm.getList("select * from activities where id_workflow = " + idWorkflow);	
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + activities.size() + " activities.");
		return activities;
	}

	
	public void updateActivity( Activity activity ) throws UpdateException {
		logger.debug("update activity...");
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		try {
			DaoFactory<Relation> dfTbl = new DaoFactory<Relation>();
			IDao<Relation> daoTbl = dfTbl.getDao( session , Relation.class);
			Relation tableIn = null;
			
			for ( Relation rel : activity.getInputRelations() ) {
				tableIn = daoTbl.getDO( rel.getIdTable() );
				activity.addInputRelation(tableIn);			
			}
			
			Relation tableOut = null;
			if ( activity.getOutputRelation().getIdTable() != -1 ) {
				tableOut = daoTbl.getDO( activity.getOutputRelation().getIdTable() );
			}
			
			activity.setOutputRelation(tableOut);
			
			fm.updateDO(activity);
			commit();
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new UpdateException( e.getMessage() );
		}
		closeSession();
		logger.debug("done.");
	}

	
	public Activity insertActivity(Activity activity) throws InsertException {
		logger.debug("insert activity...");
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		try {
			
			DaoFactory<Relation> dfTbl = new DaoFactory<Relation>();
			IDao<Relation> daoTbl = dfTbl.getDao( session , Relation.class);
			Relation tableIn = null;
			
			for ( Relation rel : activity.getInputRelations()  ) {
				logger.debug("retrieving input table " + rel.getIdTable() );
				tableIn = daoTbl.getDO( rel.getIdTable() );
				logger.debug("input table found " + tableIn.getName() );
				activity.addInputRelation(tableIn);			
			}
			
			Relation tableOut = null;
			if ( (activity.getOutputRelation() != null) && (activity.getOutputRelation().getIdTable() != -1 ) ) {
				logger.debug("retrieving output table " + activity.getOutputRelation().getIdTable() );
				tableOut = daoTbl.getDO( activity.getOutputRelation().getIdTable() );
				logger.debug("output table found: " + tableOut.getName() );
			}
			
			if ( activity.getFragment() != null ) {
				logger.debug("retrieving fragment " + activity.getFragment().getIdFragment() );
				DaoFactory<Fragment> dfEx = new DaoFactory<Fragment>();
				IDao<Fragment> daoEx = dfEx.getDao( session , Fragment.class);
				Fragment fragment = daoEx.getDO( activity.getFragment().getIdFragment() );
				logger.debug("fragment found: " + activity.getFragment().getSerial() );
				activity.setFragment(fragment);
			}
			
			activity.setOutputRelation(tableOut);
			
			fm.insertDO(activity);
			commit();
		} catch ( InsertException | NotFoundException e ) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new InsertException(e.getMessage());
		}
		closeSession();
		logger.debug("done.");
		return activity;
	}
	

	public List<Activity> getDependents( int idActivity ) throws NotFoundException {
		logger.debug("retrieving activities of " + idActivity + "..." );
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		List<Activity> activations = null;
		try {
			activations = fm.getList("select * from activities a "
					+ "join dependencies d on a.id_activity = d.activity_slave_id where d.activity_master_id = "  + idActivity );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + activations.size() + " activities.");
		return activations;
	}

	
	public Activity getActivityByTag(String tag) throws NotFoundException {
		logger.debug("retrieve Activity by tag " + tag + "..." );
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		Activity activity = null;
		try {
			activity = fm.getList("select * from activities where tag = '" + tag + "'").get(0);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done.");
		return activity;
	}	
	
	public Activity getActivity(String serial) throws NotFoundException {
		logger.debug("retrieve Activity by serial " + serial + "..." );
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		Activity activity = null;
		try {
			activity = fm.getList("select * from activities where serial = '" + serial + "'").get(0);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done.");
		return activity;
	}
	
	public Activity getActivity(int idActivity) throws NotFoundException {
		logger.debug("retrieve Activity...");
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		Activity activity = null;
		try {
			activity = fm.getDO(idActivity);
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + activity.getTag() );
		return activity;
	}
	
	public void deleteActivity(Activity activity) throws DeleteException {
		logger.debug("delete activity." );
		DaoFactory<Activity> df = new DaoFactory<Activity>();
		IDao<Activity> fm = df.getDao(this.session, Activity.class);
		try {
			fm.deleteDO(activity);
			commit();
		} catch (Exception e) {
			logger.error( e.getMessage() );
			rollBack();
			closeSession();
			throw new DeleteException( e.getMessage() );			
		}
		logger.debug("done.");
		closeSession();
	}
	

	
}
