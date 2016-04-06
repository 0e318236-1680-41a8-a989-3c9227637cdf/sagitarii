package br.cefetrj.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.cefetrj.sagitarii.persistence.entity.File;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.infra.DaoFactory;
import br.cefetrj.sagitarii.persistence.infra.IDao;

public class FileRepository extends BasicRepository {

	public FileRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<File> getList( int idExperiment ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<File> df = new DaoFactory<File>();
		IDao<File> fm = df.getDao(this.session, File.class);
		Set<File> files = null;
		try {
			files = new HashSet<File>( fm.getList("select * from files where id_experiment = " + idExperiment ) );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + files.size() + " files.");
		return files;
	}

	
	public Set<File> getList( int idExperiment, String activityTag, String rangeStart, String rangeEnd ) throws NotFoundException {
		logger.debug("get list from " + rangeStart + " to" + rangeEnd + " and activity " + activityTag );
		DaoFactory<File> df = new DaoFactory<File>();
		IDao<File> fm = df.getDao(this.session, File.class);

		Set<File> files = null;
		try {
			List<File> fils = fm.getList("select f.* from files f "
					+ "join activities a on f.id_activity = a.id_activity where f.id_experiment = " + idExperiment + 
					" and a.tag = '"+ activityTag +"' offset " +	rangeStart + " limit " + rangeEnd );
			
			files = new HashSet<File>();
			if ( fils != null ) {
				files.addAll( fils );
			}
			
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + files.size() + " light files.");
		return files;
	}
	
	public File insertFile(File file) throws InsertException {
		logger.debug("insert");
		DaoFactory<File> df = new DaoFactory<File>();
		IDao<File> fm = df.getDao(this.session, File.class);
		
		try {
			fm.insertDO(file);
			commit();
		} catch (InsertException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;
		}
		closeSession();
		logger.debug("done");
		return file;
	}
	
	public File getFile(int idFile) throws NotFoundException {
		logger.debug("get " + idFile + "...");
		DaoFactory<File> df = new DaoFactory<File>();
		IDao<File> fm = df.getDao(this.session, File.class);
		File file = null;
		try {
			file = fm.getDO(idFile);
		} catch ( Exception e ) {
			e.printStackTrace();
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + file.getFileName()  );
		return file;
	}
	
	public void deleteFile(File file) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<File> df = new DaoFactory<File>();
		IDao<File> fm = df.getDao(this.session, File.class);
		try {
			fm.deleteDO(file);
			commit();
		} catch (DeleteException e) {
			rollBack();
			closeSession();
			logger.error( e.getMessage() );
			throw e;			
		}
		logger.debug("done");
		closeSession();
	}	
	
}
