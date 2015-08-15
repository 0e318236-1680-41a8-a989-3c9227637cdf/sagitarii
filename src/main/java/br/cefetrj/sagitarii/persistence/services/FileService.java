package br.cefetrj.sagitarii.persistence.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.misc.json.JsonUserTableConversor;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.File;
import br.cefetrj.sagitarii.persistence.entity.FileLight;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.repository.FileRepository;

public class FileService {
	private FileRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	public String getFilesPagination( int idExperiment, String sortColumn, String sSortDir0,
			String iDisplayStart, String iDisplayLength, String sEcho, String sSearch) throws Exception {

		String search = "";
		String pare = "";
		if ( (sSearch != null) && ( !sSearch.equals("") ) ) {
			pare = "(";
			search = " and cast(f as text) like '%" + sSearch + "%') or (a.tag like '%" + sSearch + "%' and f.id_experiment = " + idExperiment + ") ";
		}
		
		String sql = "select f.*, a.tag as activity from files f join activities a on f.id_activity = a.id_activity "
				+ "where " + pare + "f.id_experiment = " + idExperiment + search + 
				" order by " + 
				sortColumn + " " + sSortDir0 + " offset " + iDisplayStart + " limit " + iDisplayLength ;

		String countSql = "select count(*) as qtd from files f join activities a on f.id_activity = a.id_activity "
				+ "where " + pare + "f.id_experiment = " + idExperiment + search;
		
		
		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		
		int totalRecords = 0;
		
		if ( !sortColumn.equals("ERROR") ) {
			
			RelationService rs = new RelationService();
			result = rs.genericFetchList( sql );
			
			ActivityService as = new ActivityService();
			
			
			for ( UserTableEntity ute : result  ) { // Each line of result ...
				String fileName = ute.getData("filename"); 
				String idFile = ute.getData("id_file"); 
				int idActivity = Integer.valueOf( ute.getData("id_activity") );
				
				as.newTransaction();
				Activity act = as.getActivity(idActivity);
				ute.setData("activity", act.getTag() );
				ute.setData("table", act.getOutputRelation().getName() );
				
				if ( (fileName != null) && ( !fileName.equals("") ) ) {
					ute.setData("filename", "<a href='getFile?idFile="+idFile+"'>"+fileName+"</a>");
				}
			}
			
			rs.newTransaction();
			Set<UserTableEntity> resultCount = rs.genericFetchList( countSql );
			int totR = 0;
			for ( UserTableEntity ute : resultCount  ) {
				totR = Integer.valueOf( ute.getData("qtd") );
				break;
			}
			totalRecords = totR;
			
		} else {
			Map<String,String> data = new HashMap<String,String>();
			data.put("ERROR", "No files for this experiment");
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);

		}
		return new JsonUserTableConversor().asJson( result, totalRecords, Integer.valueOf( sEcho ) );
	}

	
	
	public FileService() throws DatabaseConnectException {
		this.rep = new FileRepository();
	}

	public FileLight getFileLight(int idFile) throws NotFoundException{
		return rep.getFileLight(idFile);
	}

	public File getFile(int idFile) throws NotFoundException{
		return rep.getFile(idFile); 
	}

	public void newTransaction() {
		if( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public File insertFile(File file) throws InsertException {
		File expRet = rep.insertFile( file );
		return expRet ;
	}	
	
	public void deleteFile( int idFile ) throws DeleteException {
		try {
			FileLight file = rep.getFileLight(idFile);
			rep.newTransaction();
			rep.deleteFile(file);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
	}

	public Set<FileLight> getList( int idExperiment ) throws NotFoundException {
		Set<FileLight> preList = rep.getList( idExperiment );
		return preList;	
	}

	public Set<FileLight> getList( int idExperiment, String activityTag, String rangeStart, String rangeEnd ) throws NotFoundException {
		Set<FileLight> preList = rep.getList( idExperiment, activityTag, rangeStart, rangeEnd );
		return preList;	
	}

	
}
