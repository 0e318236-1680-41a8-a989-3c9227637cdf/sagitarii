package cmabreu.sagitarii.persistence.services;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.core.DomainStorage;
import cmabreu.sagitarii.core.ReceivedData;
import cmabreu.sagitarii.core.Sagitarii;
import cmabreu.sagitarii.core.SchemaGenerator;
import cmabreu.sagitarii.core.TableAttribute;
import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.core.config.Configurator;
import cmabreu.sagitarii.core.filetransfer.FileImporter;
import cmabreu.sagitarii.misc.DatabaseConnectionItem;
import cmabreu.sagitarii.misc.DatabaseInfo;
import cmabreu.sagitarii.misc.json.JsonUserTableConversor;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.CustomQuery;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.FileLight;
import cmabreu.sagitarii.persistence.entity.Relation;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.DeleteException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.exceptions.UpdateException;
import cmabreu.sagitarii.persistence.repository.RelationRepository;

public class RelationService { 
	private RelationRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public RelationService() throws DatabaseConnectException {
		this.rep = new RelationRepository();
	}
	
	public void copy( String table, int sourceExperiment, int targetExperiment ) throws Exception {
		logger.debug("copy table from experiment " + sourceExperiment + " to " + targetExperiment );
		Set<UserTableEntity> structure = getTableStructure( table );
		
		String tableColumns = "";
		for ( UserTableEntity ute : structure ) {
			String columnName = ute.getData("column_name");
			if ( ( !columnName.equals("id_experiment") ) && ( !columnName.equals("index_id") ) ) {
				tableColumns = tableColumns + columnName + ",";
			}
		}
		tableColumns = tableColumns.substring(0, tableColumns.length()-1);
		String selectSourceData = "select " + targetExperiment + " as id_experiment," + 
				tableColumns + " from " + table + " where id_experiment = " + sourceExperiment;
		
		String insertTargetData = "insert into " + table + "(id_experiment," + tableColumns + ")" + selectSourceData;
		logger.debug("transfer query: " + insertTargetData );
		
		newTransaction();
		executeQuery( insertTargetData );
		
	}
	
	public void commitAndClose() throws Exception {
		try {
			rep.commit();
			rep.closeSession();
		} catch ( Exception e ) {
			rep.rollBack();
			rep.closeSession();
			throw e;
		}
	}

	public void rollbackAndClose() throws Exception {
		try {
			rep.rollBack();
			rep.closeSession();
		} catch ( Exception e ) {
			throw e;
		}
	}

	// Get the first 40 lines and export as CSV to the user.
	public ByteArrayInputStream getTableSample( String tableName ) throws Exception {
		Set<UserTableEntity> utes = genericFetchList( "select * from " + tableName + " offset 0 limit 40" );
		StringBuilder columns = new StringBuilder();
		StringBuilder dataLine = new StringBuilder();
		boolean columnsDone = false;
		StringBuilder csvData = new StringBuilder(); 
		for ( UserTableEntity ute : utes ) {
			String prefix = "";
			for ( String column : ute.getColumnNames() ) {
				String data = ute.getData( column );
				if( !columnsDone ) { columns.append( prefix + column ); }
				dataLine.append( prefix + data );
				prefix = ",";
			}
			if( !columnsDone ) { csvData.append( columns.toString() + "\n" ); }
			csvData.append( dataLine.toString() + "\n");
			dataLine.setLength(0);
			columnsDone = true;
		}
        return new ByteArrayInputStream( csvData.toString().getBytes("UTF-8") );
	}
	
	// Get the all lines and export as CSV to the user.
	public ByteArrayInputStream getTableFull( String tableName, int idExperiment ) throws Exception {
		Set<UserTableEntity> utes = genericFetchList( "select * from " + tableName + " where id_experiment = " + idExperiment );
		StringBuilder columns = new StringBuilder();
		StringBuilder dataLine = new StringBuilder();
		boolean columnsDone = false;
		StringBuilder csvData = new StringBuilder(); 
		for ( UserTableEntity ute : utes ) {
			String prefix = "";
			for ( String column : ute.getColumnNames() ) {
				String data = ute.getData( column );
				if( !columnsDone ) { columns.append( prefix + column ); }
				dataLine.append( prefix + data );
				prefix = ",";
			}
			if( !columnsDone ) { csvData.append( columns.toString() + "\n" ); }
			csvData.append( dataLine.toString() + "\n");
			dataLine.setLength(0);
			columnsDone = true;
		}
        return new ByteArrayInputStream( csvData.toString().getBytes("UTF-8") );
	}
	
	
	
	// ======================== TABLE JSON REQUESTS ========================================================

	// ======================== CUSTOM QUERY ===============================================================
	public String inspectExperimentQueryPagination(CustomQuery query, String sortColumn, String sSortDir0,
			String iDisplayStart, String iDisplayLength, String sEcho) throws Exception {
		
		logger.debug("pagination inspectExperimentQuery");

		String sql = "select * from (" + query.getQuery() + ") as qq order by " + 
				sortColumn + " " + sSortDir0 + " offset " + iDisplayStart + " limit " + iDisplayLength ;

		logger.debug( sql );
		
		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		int totalRecords = 0;
		
		if ( !sortColumn.equals("ERROR") ) {
			result = genericFetchList( sql );
			newTransaction();
			
			String countSql = "select count(*) as count from (" + query.getQuery() + ") as qq";
			Set<UserTableEntity> resultCount = genericFetchList( countSql );

			for ( UserTableEntity ute : result  ) {
				for ( String columnName : ute.getColumnNames() ) {
					String data = ute.getData( columnName ); 
					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);
				}
			}
			
			UserTableEntity count = resultCount.iterator().next();
			totalRecords = Integer.valueOf( count.getData("count") ); 
		} else {
			Map<String,String> data = new HashMap<String,String>();
			String warning = "No data in query '" + query.getName() + "'";
			byte[] encodedBytes = Base64.encodeBase64( warning.getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		}
		return new JsonUserTableConversor().asJson( result, totalRecords, Integer.valueOf( sEcho ) );
	}
	
	// ======================== INSPECT EXPERIMENT TABLE ====================================================
	public Set<UserTableEntity> inspectExperimentTable( String tableName, Experiment experiment ) throws Exception {
		String sql = "select * from " + tableName + " where id_experiment = " + experiment.getIdExperiment();
		Set<UserTableEntity> result = genericFetchList( sql );

		if ( result.size() == 0 ) {
			Map<String,String> data = new HashMap<String,String>();
			byte[] encodedBytes = Base64.encodeBase64( "No Data".getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		} else {
		}
		
		return result;
	}
	
	public String inspectExperimentTablePagination(String tableName, int idExperiment, String sortColumn, String sSortDir0,
			String iDisplayStart, String iDisplayLength, String sEcho, String sSearch) throws Exception {
		
		logger.debug("pagination inspectExperimentTable");
		
		String search = "";
		if ( (sSearch != null) && ( !sSearch.equals("") ) ) {
			search = " and cast("+tableName+" as text) like '%" + sSearch + "%'";
		}		
		
		String sql = "select * from " + tableName + " where id_experiment = " + idExperiment + search +
				" order by " + sortColumn + " " + sSortDir0 + " offset " + iDisplayStart + 
				" limit " + iDisplayLength ;

		logger.debug( sql );
		
		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		int totalRecords = 0;
		
		if ( !sortColumn.equals("ERROR") ) {
			result = genericFetchList( sql );
			FileService fs = new FileService();

			// Show ID instance as a link
			for ( UserTableEntity ute : result  ) {
				List<String> columns = ute.getColumnNames();
				for ( String columnName : columns ) {
					String domainName = tableName + "." + columnName;
					String data = ute.getData( columnName ); 
					
					if ( DomainStorage.getInstance().domainExists(domainName)  ) {
						fs.newTransaction();
						FileLight fil = fs.getFileLight( Integer.valueOf( ute.getData(columnName) ) );
						data = "<a href='getFile?idFile="+data+"'>" + fil.getFileName() + "</a>";
					}

					if ( columnName.equals("id_instance") ) {
						String idInstance = data; 
						if ( (idInstance != null) && ( !idInstance.equals("") ) ) {
							data = "<a href='viewInstance?tableName="+tableName+"&idExperiment="+idExperiment+"&idInstance="+idInstance+"'>"+idInstance+"</a>";
						} else {
							data = "n/e";
						}
					}
					
					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);
					
				}
				
			}
			
			newTransaction();

			logger.debug( "get count from " + tableName + " where id_experiment = " + idExperiment + search );

			totalRecords = getCount( tableName, "where id_experiment = " + idExperiment + search);
		} else {
			Map<String,String> data = new HashMap<String,String>();
			String message = "No data in table " + tableName + " for this experiment";
			byte[] encodedBytes = Base64.encodeBase64( message.getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);			
			

		}
		return new JsonUserTableConversor().asJson( result, totalRecords, Integer.valueOf( sEcho ) ).replace("\\", "\\\\");
	}
	
	// ======================== CUSTOM SQL TERMINAL =========================================================
	
	public String runUserSqlPagination(String sql, String sortColumn, String sSortDir0,
			String iDisplayStart, String iDisplayLength, String sEcho, String sSearch) throws Exception {

		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		int totalRecords = 0;
		
		if ( !sortColumn.equals("ERROR") ) {
			result = genericFetchList( "select * from ("+sql+") as t1 order by " + 
				sortColumn + " " + sSortDir0 + " offset " + iDisplayStart + " limit " + iDisplayLength ); 
			
			for ( UserTableEntity ute : result  ) {
				for ( String columnName : ute.getColumnNames() ) {
					String data = ute.getData( columnName ); 
					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);
				}
			}
			
			newTransaction();
			totalRecords = getCount( sql );
		} else {
			Map<String,String> data = new HashMap<String,String>();
			byte[] encodedBytes = Base64.encodeBase64( "No Data".getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		}
		return new JsonUserTableConversor().asJson( result, totalRecords, Integer.valueOf( sEcho ) ).replace("\\", "\\\\");
	}
	
	public Set<UserTableEntity> runUserSql( String sql ) throws Exception {
		Set<UserTableEntity> result = genericFetchList( sql );
		if ( result.size() == 0 ) {
			Map<String,String> data = new HashMap<String,String>();
			byte[] encodedBytes = Base64.encodeBase64( "No Data".getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		} else {
			//
		}
		
		return result;
	}
	
	// ======================== VIEW SQL ( INSPECT TABLE ) =====================================================
	public Set<UserTableEntity> viewSql( String tableName ) throws Exception {
		String sql = "select * from " + tableName;
		Set<UserTableEntity> result = genericFetchList( sql );
		if ( result.size() == 0 ) {
			Map<String,String> data = new HashMap<String,String>();
			byte[] encodedBytes = Base64.encodeBase64( "No Data".getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		}
		return result;
	}
	
	
	public String viewSqlPagination(String tableName, String sortColumn, String sSortDir0,
			String iDisplayStart, String iDisplayLength, String sEcho, String sSearch) throws Exception {
		
		logger.debug("pagination viewSql");
		
		String search = " where 1 = 1";
		if ( (sSearch != null) && ( !sSearch.equals("") ) ) {
			search = " where cast("+tableName+" as text) like '%" + sSearch + "%'";
		}		
		
		String sql = "select * from " + tableName + search + " order by " + 
				sortColumn + " " + sSortDir0 + " offset " + iDisplayStart + " limit " + iDisplayLength ;

		logger.debug( sql );

		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		int totalRecords = 0;
		
		if ( !sortColumn.equals("ERROR") ) {
			result = genericFetchList( sql );
			FileService fs = new FileService();
			
			for ( UserTableEntity ute : result  ) { // Each line of result ...
				List<String> columns = ute.getColumnNames();
				for ( String columnName : columns ) {
					String domainName = tableName + "." + columnName;
					String data = ute.getData( columnName ); 
					
					if ( DomainStorage.getInstance().domainExists(domainName)  ) {
						fs.newTransaction();
						FileLight fil = fs.getFileLight( Integer.valueOf( ute.getData(columnName) ) );
						data = "<a href='getFile?idFile="+data+"'>" + fil.getFileName() + "</a>";
					}

					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);					
				}
			}
			newTransaction();
			logger.debug("get count from " + tableName + " where " + search );
			totalRecords = getCount( tableName, search);
			
		} else {
			Map<String,String> data = new HashMap<String,String>();
			String warning = "No data in table " + tableName + " for this experiment";
			byte[] encodedBytes = Base64.encodeBase64( warning.getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		}
		return new JsonUserTableConversor().asJson( result, totalRecords, Integer.valueOf( sEcho ) ).replace("\\", "\\\\");
	}

	
	// ======================================================================================================
	
	
	public Set<UserTableEntity> getGeneratedData( String tableName, int idInstance, int idExperiment ) throws Exception {
		String sql = "select * from " + tableName + " where id_instance = " + idInstance;
		Set<UserTableEntity> result = genericFetchList( sql );
		if ( result.size() == 0 ) {
			Map<String,String> data = new HashMap<String,String>();
			String warning = "No data in table " + tableName + " for this instance";
			byte[] encodedBytes = Base64.encodeBase64( warning.getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		} else {
			for ( UserTableEntity ute : result ) {
				for ( String columnName : ute.getColumnNames() ) {
					String data = ute.getData( columnName ); 
					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);
				}
			}
		}
		return result;
	}

	
	public Set<UserTableEntity> getConsumptionsData( Set<Consumption> consumptions, int idExperiment ) throws Exception {
		Set<UserTableEntity> result = new HashSet<UserTableEntity>();
		for ( Consumption consumption : consumptions ) {
			int idTable = consumption.getIdTable();
			newTransaction();
			Relation relation = getTable(idTable);
			String sql = "select t.name as table_name,u.* from " + relation.getName() + " u join tables t on t.id_table = "+idTable+" where index_id = " + consumption.getIdRow();
			newTransaction();
			result.addAll( genericFetchList( sql ) );
		}
		
		if ( result.size() == 0 ) {
			Map<String,String> data = new HashMap<String,String>();
			byte[] encodedBytes = Base64.encodeBase64( "No consumptions found for this instance".getBytes() );
			String newData = new String( encodedBytes );
			data.put("ERROR", newData);
			UserTableEntity ute = new UserTableEntity(data);
			result.add(ute);
		} else {
			FileService fs = new FileService();
			for ( UserTableEntity ute : result ) {
				String tableName = ute.getData("table_name");
				List<String> columns = ute.getColumnNames();
				for ( String columnName : columns ) {
					String domainName = tableName + "." + columnName;
					String data = ute.getData( columnName );
					
					if ( DomainStorage.getInstance().domainExists(domainName)  ) {
						fs.newTransaction();
						FileLight fil = fs.getFileLight( Integer.valueOf( data ) );
						data = "<a href='getFile?idFile="+data+"'>" + fil.getFileName() + "</a>";
					}

					if ( columnName.equals("id_instance") ) {
						String sIdInstance = data; 
						if ( (sIdInstance != null) && ( !sIdInstance.equals("") ) ) {
							data = "<a href='viewInstance?tableName="+tableName+"&idExperiment="+idExperiment+"&idInstance="+sIdInstance+"'>"+sIdInstance+"</a>";
						} else {
							data = "n/e";
						}					
					}
					
					byte[] encodedBytes = Base64.encodeBase64( data.getBytes() );
					String newData = new String( encodedBytes );
					ute.setData(columnName, newData);
					
				}				
				
			}
		}

		return result;
	}

	
	public void newTransaction() {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
	}
	
	public int insertTable(Relation table, List<TableAttribute> attributes) throws Exception{
		
		table.setName( table.getName().toLowerCase().replace(" ", "") );
		
		if ( Character.isDigit( table.getName().charAt(0) ) ) {
			throw new Exception("Table name cannot start with numbers.");
		}

		for ( TableAttribute attr : attributes) {
			attr.setName( attr.getName().toLowerCase().replace(" ", "") );
			if ( Character.isDigit( attr.getName().charAt(0) )  ) {
				throw new Exception("Attribute name cannot start with numbers ("+attr.getName()+").");
			}
		}
		logger.debug("generating schema for " + table.getName() );
		table.setSchema( SchemaGenerator.generateSchema( table.getName(), attributes) );
		logger.debug("inserting relation " + table.getName() + "..." );
		rep.insertTable( table, attributes );
		logger.debug("done inserting relation " + table.getName() );
		
		Sagitarii.getInstance().stopProcessing();
		try {
			RelationRepository newRepo = new RelationRepository();
			logger.debug("executing DDL schema creation for " + table.getName() + "..." );
			newRepo.createDatabaseTable( table.getSchema() );
			logger.debug("finished table creation for table " + table.getName() + ". Creating index..."  );
			newRepo.newTransaction();
			newRepo.createInternalIndex( table.getName() );
			
		} finally {
			Sagitarii.getInstance().resumeProcessing();
		}
		
		return 0;
	}

	
	public void executeQuery(String query) throws Exception {
		if ( !rep.isOpen() ) {
			newTransaction();
		}
		rep.executeQuery(query);
	}
	
	public void executeQueryAndKeepOpen(String query) throws Exception {
		if ( !rep.isOpen() ) {
			newTransaction();
		}
		rep.executeQueryAndKeepOpen(query);
	}

	/**
	 * Returns a table structure 
	 */
	public Set<UserTableEntity> getTableStructure(String tableName) throws Exception {
		logger.debug("get schema from " + tableName );
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
		return genericFetchList("SELECT column_name, data_type FROM information_schema.columns WHERE "
				+ "table_schema <> 'information_schema' and "
				+ "table_name='"+tableName+"'");
	}
	
	
	/**
	 * Get the system open database connections 
	 * 
	 */
	public List<DatabaseConnectionItem> getConnectionUse() throws Exception {
		logger.debug("get database connection use");
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
		Set<UserTableEntity> connections = genericFetchList("select application_name, query_start, state_change, query, datname, state FROM pg_stat_activity");
		List<DatabaseConnectionItem> result = new ArrayList<DatabaseConnectionItem>();
		for ( UserTableEntity conn : connections ) {
			// exclude this query from result
			if ( !conn.getData("query").contains("select application_name,") ) {
				DatabaseConnectionItem query = new DatabaseConnectionItem(conn.getData("application_name"), conn.getData("query_start"), conn.getData("datname"),
						conn.getData("state"), conn.getData("query") );
				result.add( query );
			}
		}
		return result;
	}

	
	public int getCount( String tableName, String criteria ) throws Exception {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
		return rep.getCount( tableName, criteria );
	}

	public int getCount( String sql ) throws Exception {
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
		
		List<UserTableEntity> res = new ArrayList<UserTableEntity> ( 
				genericFetchList("select count (*) as qtd from ( "+sql+" ) as t1") );
		int qtd = 0;
		if ( res.size() > 0 ) {
			UserTableEntity ute = res.get(0);
			String sQtd = ute.getData("qtd");
			qtd = Integer.valueOf( sQtd );
		}
		return qtd;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Set<UserTableEntity> genericFetchList(String query) throws Exception {
		logger.debug("generic fetch " + query );
		if ( !rep.isOpen() ) {
			rep.newTransaction();
		}
		Set<UserTableEntity> result = new LinkedHashSet<UserTableEntity>();
		for ( Object obj : rep.genericFetchList(query) ) {
			UserTableEntity ut = new UserTableEntity( (Map)obj );
			result.add(ut);
		}
		return result;
	}
	
	
	/**
	 * Verifica se uma coluna de determinado nome existe na estrutura de uma tabela.
	 */
	private boolean attributeExists( String attribute, Set<UserTableEntity> structure ) {
		for ( UserTableEntity ute : structure  ) {
			if ( ute.hasContent( attribute ) ) {
				return true;
			}
		}
		return false;
	}
	
	public void updateTable(Relation table) throws UpdateException {
		Relation oldTable;
		try {
			oldTable = rep.getTable( table.getIdTable() );
		} catch (NotFoundException e) {
			logger.debug( e.getMessage() );
			throw new UpdateException( e.getMessage() );
		}
		oldTable.setDescription( table.getDescription() );
		rep.newTransaction();
		rep.updateTable(oldTable);
	}	

	
	/**
	 * Receive the CSV data from Teapot
	 */
	public void importCSVData(ReceivedData rd, FileImporter importer) throws Exception {
		String sql = "";
		List<String> contentLines = rd.getContentLines();
		
		ExperimentService es = new ExperimentService();
		Experiment owner = es.getExperiment( rd.getCsvDataFile().getExperimentSerial() );
		
		
		// Get table schema: Time consuming + One more database hit!! Need to find a way to avoid this.
		String output = rd.getActivity().getOutputRelation().getName();
		Set<UserTableEntity> structure = getTableStructure(output);
		// ===================
		
		if ( importer != null ) {
			importer.setActivity( rd.getActivity().getTag() );
		}
		
		
		logger.debug("received " + contentLines.size() + " lines of data from teapot. instance " + rd.getInstance().getIdInstance() + " | activity " + rd.getActivity().getIdActivity() );
		for ( int x = 1; x < contentLines.size(); x++ ) {
			
			String ss = contentLines.get(x);
			
			logger.debug("data: " + ss);
			
			if ( (ss == null) || (ss.equals("" )) ) {
				continue;
			}
			
			String values = "";
			String columns = "id_experiment,id_activity,id_instance," ;
			
			char delimiter = Configurator.getInstance().getCSVDelimiter();
			String emptyData = delimiter + "" + delimiter;
			String nullData = delimiter + "null" + delimiter;

			// Is the middle value null?
			String contentDataLine = contentLines.get(x).replace(emptyData, nullData);
			
			// Is the last value null? 
			if ( contentDataLine.endsWith( String.valueOf( delimiter ) ) ) {
				contentDataLine = contentDataLine + "null";
			}
			// Is the first value null? 
			if ( contentDataLine.startsWith( String.valueOf( delimiter ) ) ) {
				contentDataLine = "null" + contentDataLine;
			}
			// =============================
			
			String[] columnsArray = contentLines.get(0).split( String.valueOf( Configurator.getInstance().getCSVDelimiter() ) );
			String[] valuesArray = contentDataLine.split( String.valueOf( delimiter ) );
			
			for ( int z = 0; z < columnsArray.length; z++ ) {
				try {
					if ( attributeExists( columnsArray[z], structure ) ) {
						columns = columns + columnsArray[z] + ",";
						if ( valuesArray[z].equals("null") ) {
							values = values + valuesArray[z] + ",";
						} else {
							values = values + "'" + valuesArray[z] + "',";
						}
					}
				} catch ( Exception ex ) {
					logger.error("Error when inserting data into table " + rd.getTable().getName() );
					ex.printStackTrace();
				}
			}
			if ( values.trim().length() > 0 ) {
				values = values.substring(0, values.length()-1);
			} else {
				throw new Exception("None of your CSV columns match table '"+output+"' attribute list. Is this the right table?");
			}

			sql = "insert into " + rd.getTable().getName() + "("+columns.substring(0, columns.length()-1).toLowerCase()+") values ("+ owner.getIdExperiment() + 
					"," + rd.getActivity().getIdActivity() + "," + rd.getInstance().getIdInstance() + "," + values + ");";
			
			executeQueryAndKeepOpen(sql);
			if ( importer != null ) {
				if ( importer.forcedToStop() ) {
					rollbackAndClose();
					throw new Exception("Canceled by the user");
				}
				importer.setInsertedLines( x );
			}
			
		}
		commitAndClose();
	}

	public Relation getTable(String name) throws NotFoundException{
		return rep.getTable(name);
	}
	
	public Relation getTable(int idTable) throws NotFoundException{
		return rep.getTable(idTable);
	}
	
	public String deleteTable( int idTable ) throws DeleteException {
		logger.debug( "delete table " + idTable );  
		String tableName = "";
		try {
			Relation table = rep.getTable(idTable);

			if ( getCount( table.getName(), "") > 0 ) {
				throw new DeleteException( "Table " + table.getName() + " is not empty and cannot be deleted." );
			}
			
			rep.newTransaction();
			rep.deleteTable(table);
			tableName = table.getName();
		} catch (Exception e) {
			logger.error( e.getMessage() );  
			throw new DeleteException( e.getMessage() );
		}
		logger.debug( "done." );
		return tableName;
	}
	

	public void copyDatabase(DatabaseInfo source, DatabaseInfo target) throws Exception {
		sendData("users", source, target);
		sendData("executors",  source, target);
		sendData("tables",  source, target);
		sendData("domains",  source, target);
		sendData("fragments",  source, target);
		sendData("activities",  source, target);
		sendData("workflows",  source, target);
		sendData("experiments",  source, target);
		sendData("dependencies",  source, target);
		sendData("instances",  source, target);
		sendData("files",  source, target);
		sendData("consumptions",  source, target);
		sendData("queries",  source, target);
		sendData("relationship",  source, target);
		sendData("timecontrol",  source, target);
	}
	
	
	private void sendData(String tableName, DatabaseInfo source, DatabaseInfo target) throws Exception {
		String myHost = source.getHost();
		String myPort = source.getPort();
		String myUser = source.getUser();
		String myDb = source.getDb();
		String myPassword = source.getPassword();

		String targetHost = target.getHost();
		String targetPort = target.getPort();
		String targetUser = target.getUser();
		String targetDb = target.getDb();
		String targetPassword = target.getPassword();
		
		/*
		 * CREATE EXTENSION dblink
		 * 
		 * select * from dblink_exec('hostaddr=127.0.0.1 dbname=testea port=5432 user=postgres password=admin',
				'insert into teste select * from dblink(''hostaddr=127.0.0.1 dbname=testeb port=5432 user=postgres password=admin'', 
				''select id,nome from teste'') as t1(a integer,b text);');
		 *
		 *
		*/
		
		Set<UserTableEntity> fields = getTableStructure( tableName );
		StringBuilder fieldNames = new StringBuilder();
		StringBuilder fieldDefs = new StringBuilder();
		String prefix = "";
		for ( UserTableEntity ute : fields ) {
			String columnName = ute.getData("column_name");
			String dataType = ute.getData("data_type");
			if( dataType.equals("character varying") ) {
				dataType = "text";
			}
			fieldNames.append( prefix + columnName );
			fieldDefs.append( prefix + columnName + " " + dataType );
			prefix = ",";
		}
			
		String remoteSql = "select " + fieldNames.toString() + " from " + tableName;
		String remoteDef = fieldDefs.toString();
		
		String copyQuery = "truncate table "+tableName+" cascade; insert into " + tableName + " select * from dblink(''hostaddr="+myHost+
				" dbname="+myDb+" port="+myPort+" user="+myUser+" password="+myPassword+"''," +
				"''"+remoteSql+"'') as " + "t1("+remoteDef+");";

		String toRun = "select * from dblink_exec('hostaddr="+targetHost+" dbname="+targetDb+" port="+targetPort+
				" user="+targetUser+" password="+targetPassword+"','"+copyQuery+"');";
		
		executeQuery( toRun );
	}
	
	
	public List<Relation> getList() throws NotFoundException {
		return rep.getList();	
	}	
}
