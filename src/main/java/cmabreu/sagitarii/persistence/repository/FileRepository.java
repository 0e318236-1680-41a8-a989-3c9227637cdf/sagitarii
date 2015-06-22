package cmabreu.sagitarii.persistence.repository;

import java.util.HashSet;
import java.util.Set;

import cmabreu.sagitarii.persistence.entity.File;
import cmabreu.sagitarii.persistence.entity.FileLight;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.infra.DaoFactory;
import cmabreu.sagitarii.persistence.infra.IDao;

public class FileRepository extends BasicRepository {

	public FileRepository() throws DatabaseConnectException {
		super();
		logger.debug("init");
	}

	public Set<FileLight> getList( int idExperiment ) throws NotFoundException {
		logger.debug("get list" );
		DaoFactory<FileLight> df = new DaoFactory<FileLight>();
		IDao<FileLight> fm = df.getDao(this.session, FileLight.class);
		Set<FileLight> files = null;
		try {
			files = new HashSet<FileLight>( fm.getList("select * from files where id_experiment = " + idExperiment ) );
		} catch ( Exception e ) {
			closeSession();
			throw e;
		}
		closeSession();
		logger.debug("done: " + files.size() + " files.");
		return files;
	}

	
	public Set<FileLight> getList( int idExperiment, String activityTag, String rangeStart, String rangeEnd ) throws NotFoundException {
		logger.debug("get list from " + rangeStart + " to" + rangeEnd + " and activity " + activityTag );
		DaoFactory<FileLight> df = new DaoFactory<FileLight>();
		IDao<FileLight> fm = df.getDao(this.session, FileLight.class);

		Set<FileLight> files = null;
		try {
			files = new HashSet<FileLight>( fm.getList("select f.* from files f "
					+ "join activities a on f.id_activity = a.id_activity where f.id_experiment = " + idExperiment + 
					" and a.tag = '"+ activityTag +"' offset " +	rangeStart + " limit " + rangeEnd ) );
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
		logger.debug("done: " + file.getFileName() + ": " + file.getFile().length + " bytes (GZIPPED)" );
		return file;
	}
	
	public FileLight getFileLight(int idFile) throws NotFoundException {
		logger.debug("get light file " + idFile + "...");
		DaoFactory<FileLight> df = new DaoFactory<FileLight>();
		IDao<FileLight> fm = df.getDao(this.session, FileLight.class);
		FileLight file = null;
		try {
			file = fm.getDO(idFile);
		} catch ( Exception e ) {
			e.printStackTrace();
			closeSession();		
			throw e;
		} 
		closeSession();		
		logger.debug("done: " + file.getFileName() );
		return file;
	}

	public void deleteFile(FileLight file) throws DeleteException {
		logger.debug("delete" );
		DaoFactory<FileLight> df = new DaoFactory<FileLight>();
		IDao<FileLight> fm = df.getDao(this.session, FileLight.class);
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
