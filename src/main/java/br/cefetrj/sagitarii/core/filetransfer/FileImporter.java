package br.cefetrj.sagitarii.core.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.DataReceiver;
import br.cefetrj.sagitarii.core.DomainStorage;
import br.cefetrj.sagitarii.core.NodesManager;
import br.cefetrj.sagitarii.core.ReceivedData;
import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.types.ActivityStatus;
import br.cefetrj.sagitarii.core.types.ActivityType;
import br.cefetrj.sagitarii.core.types.InstanceStatus;
import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ActivityService;
import br.cefetrj.sagitarii.persistence.services.InstanceService;
import br.cefetrj.sagitarii.persistence.services.RelationService;

public class FileImporter extends Thread {
	private Server server;
	private String sessionSerial;
	private String sessionContext;
	private List<ReceivedFile> receivedFiles;
	private Map<String, Integer> fileIds;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private Date startTime;
	private String log;
	private long importedLines = 0;
	private String status;
	private ReceivedFile mainCsvFile;
	private long insertedLines = 0;
	private int percent;
	private String tag;
	private boolean forceStop = false;
	private long totalFiles;
	private boolean initialLoad = false;
	private String instance;
	private String activity;
	private String fragment;
	private long importedFiles = 0;
	private String lastImportedFile = "";
	
	public String getLastImportedFile() {
		return lastImportedFile;
	}
	
	public long getImportedFiles() {
		return importedFiles;
	}
	
	public String getInstance() {
		return instance;
	}
	
	public String getActivity() {
		return activity;
	}
	
	public String getFragment() {
		return fragment;
	}
	 
	public void stopProcess() {
		forceStop = true;
	}
	
	public List<ReceivedFile> getReceivedFiles() {
		return new ArrayList<ReceivedFile>( receivedFiles );
	}
	
	public boolean forcedToStop() {
		return forceStop;
	}
	
	public String getTag() {
		return tag;
	}
	

	public long getTotalFiles() {
		try {
			totalFiles = receivedFiles.size();
		} catch ( Exception e ) {
			totalFiles = 0;
		}
		return totalFiles; 
	}
	
	public void setActivity( String activity ) {
		mainCsvFile.setActivity( activity );
	}
	
	public int getPercent() {
		try {
			percent = Math.round( (insertedLines * 100 ) / importedLines );
		} catch ( Exception e ) { percent = 0; }
		return percent;
	}
	
	public ReceivedFile getMainCsvFile() {
		return mainCsvFile;
	}
	
	public long getInsertedLines() {
		return insertedLines;
	}
	
	public void setInsertedLines(long insertedLines) {
		this.insertedLines = insertedLines;
	}
	
	public String getStatus() {
		return status;
	}
	
	public long getImportedLines() {
		return importedLines;
	}
	
	public String getLog() {
		return log;
	}

	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public FileImporter(  String sessionSerial, Server server ) throws Exception {
		this.tag = sessionSerial;
		this.server = server;
		this.startTime = Calendar.getInstance().getTime();
		this.sessionSerial = sessionSerial;
		this.sessionContext = PathFinder.getInstance().getPath() + "/cache/" + sessionSerial;
		this.fileIds = new HashMap<String,Integer>();
		this.status = "WORKING";
	}

	
	private void cleanFiles() {
		logger.debug("Cleaning session files...");
		try {
			if ( receivedFiles.size() > 0 ) {
				for( ReceivedFile receivedFile : receivedFiles ) {
					String name = receivedFile.getFileName();
					try {
						Integer fileID = fileIds.get( name );
						logger.error(" > " + fileID + ": " + name);
					} catch ( Exception ignored ) {
						//
					}
				}
			}
		} catch ( Exception e ) {
			//
		}
	}
	
	/*
	// TODO: Move to storage
	private int importFile( String experimentSerial, String fileName, Activity activity, Instance instance ) {
		log = "Storing file " + fileName + " to database";
		logger.debug( log + " : Experiment " + experimentSerial + " Activity " + activity.getSerial() + " Instance " + instance.getSerial()  );
		int response = -1;
		try {
			String fullFile = sessionContext + "/" + fileName;
			File sourceFile = new File( fullFile + ".gz" );
			if ( !sourceFile.exists() ) {
				throw new Exception( "File " + fullFile + " not found." ); 
			}
			
			ExperimentService ex = new ExperimentService();
			Experiment experiment = ex.getExperiment( experimentSerial );
			FileService fs = new FileService();

			String storageTargetPath = PathFinder.getInstance().getPath() + "/storage/" ; 

			br.cefetrj.sagitarii.persistence.entity.File file = new br.cefetrj.sagitarii.persistence.entity.File();
			file.setExperiment(experiment);
			file.setFileName( fileName );
			file.setActivity( activity );
			file.setInstance( instance );
			file.setFilePath( storageTargetPath );
			
			fs.insertFile(file);
			
			response = file.getIdFile();

			File fil = new File( storageTargetPath + response + "/" );
			fil.mkdirs();
			
			File targetFile = new File( storageTargetPath + "/" + response + "/" + fileName + ".gz" );
			Files.copy(sourceFile.toPath(), targetFile.toPath());
			
			importedFiles++;
			
			logger.debug( "ID " + response + " assigned to File "+fileName+" : Experiment " + experimentSerial + " Activity " + activity.getSerial() + " Instance " + instance.getSerial()  );
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
		}
		log = fileName + " stored.";
		return response;
	}
	*/
	
	private Activity retrieveActivity( String activitySerial, String macAddress, Relation table ) throws Exception {
		ActivityService as = new ActivityService();
		Activity activity = null;
		try {
			activity = as.getActivity( activitySerial );
			logger.debug("found activity " + activity.getSerial() );
		} catch ( NotFoundException nf ) {
			initialLoad = true;

			activity = new Activity();
			activity.setDescription( table.getName() );
			activity.setTag( "DATA_LOADER" );
			activity.setType( ActivityType.LOADER );
			activity.setExecutorAlias("DATA_LOADER");
			activity.setStatus( ActivityStatus.FINISHED );
			activity.setOutputRelation(table);
			
			as.newTransaction();
			as.insertActivity(activity);
			logger.debug("activity " + activitySerial + " not found. New ID generated: " + activity.getSerial() );
		}
		return activity;
	}
	
	private Instance retrieveInstance( String instanceSerial, Activity activity ) throws Exception {
		InstanceService ps = new InstanceService();
		Instance instance = null;
		try {
			instance = ps.getInstance( instanceSerial );
			logger.debug("found instance " + instance.getSerial() );
		} catch ( NotFoundException nf ) {
			instance = new Instance();
			instance.setType( ActivityType.LOADER );
			instance.setStatus( InstanceStatus.NEW_DATA );
			instance.setStartDateTime( Calendar.getInstance().getTime() );
			instance.setFinishDateTime( Calendar.getInstance().getTime() );

			ps.newTransaction();
			ps.insertInstance(instance);
			logger.debug("instance " + instanceSerial + " not found. New ID generated: " + instance.getSerial() );
		}
		return instance;
	}
	

	private void importData( ReceivedFile csvDataFile ) throws Exception {
		String newDataFile = csvDataFile.getFileName() + ".uncompressed";
		decompress( sessionContext + "/" + csvDataFile.getFileName(), sessionContext + "/" + newDataFile );
		mainCsvFile = csvDataFile;
		File csvData = new File( sessionContext + "/" + newDataFile );
		logger.debug("will import data from file " + newDataFile );
		if ( !csvData.exists() ) {
			logger.error("file " + csvDataFile.getFileName() + " not found");
			return;
		}
		
		CSVFormat format = CSVFormat.RFC4180.withDelimiter( Configurator.getInstance().getCSVDelimiter() );
		CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), format );
		CSVRecord headerLine = null;
		
		String relationName = csvDataFile.getTargetTable();
		String activitySerial = csvDataFile.getActivity();
		String instanceSerial = csvDataFile.getInstance();
		String macAddress = csvDataFile.getMacAddress();
		String experimentSerial = csvDataFile.getExperimentSerial();
		
		logger.debug("start CSV data import: relation " + relationName + " activity: " + activitySerial + " instance: " + instanceSerial);
		
		RelationService relationService = new RelationService();
		Relation table = null;
		try {
			table = relationService.getTable( relationName );
			logger.debug("target table found: " + table.getName() );
		} catch ( NotFoundException e ) {
			throw new Exception("Table '" + relationName + "' not found in database.");
		}

		Activity activity = retrieveActivity(activitySerial,macAddress, table); // Resp. por sinalizar initialLoad
		Instance instance = retrieveInstance(instanceSerial, activity);

		if ( instance.getStatus() == InstanceStatus.FINISHED ) {
			logger.debug("instance " + instance.getSerial() + " already done. aborting...");
			return;
		}
		
		logger.debug("will import instance " + instance.getSerial() + " with current status of " + instance.getStatus() );
		
		List<String> contentLines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		boolean headerReady = false;

		log = "Importing CSV data";
		logger.debug("checking CSV data...");
		// Parsing lines...
		for (CSVRecord csvRecord : parser) {
			
			importedLines++;
			if ( headerLine == null ) { 
				headerLine = csvRecord; 
			}
			
			// For each line, parsing columns...
			for ( int x = 0; x < csvRecord.size(); x++ ) {
				// Mount the columns line (line 0)
				if ( !headerReady ) {
					String columnName = csvRecord.get(x).replace("'", "`");
					sb.append( prefix + columnName );
					prefix = ",";
					continue;
				}

				// Check if any field of csv is a reference to a file
				// ******************** THIS PART WAS CHANGED TO USE HDFS ****************************
				String columnName = headerLine.get(x); 							// <<---- Column Name
				// Get the column content 
				String columnContent = csvRecord.get(x).replace("'", "`"); 	
				// Is this column of file type?
				if ( DomainStorage.getInstance().domainExists(relationName + "." + columnName) ) {
					// File exists in session folder ( FTP from upload tool )
					String fullFilePath = sessionContext + "/" + columnContent; 	// Full path to session local file
					String hdfsFileTargetFolder = experimentSerial + "/" + sessionSerial;	// HDFS final file destination
					
					
					File fil = new File( fullFilePath );
					if ( fil.exists() ) {
						// We have a file from upload tool! Need to store it into HDFS...
						// TODO: Store file into HDFS
						// check if already stored:
						if ( fileIds.get( columnContent ) == null ) {
							// No stored yet. store it to HDFS
							// storeToHdfs( <localFile>, <targetFolder> );
							// storeToHdfs( fullFilePath, hdfsFileTargetFolder );
							// fileIds.put( columnContent, x );
						}
						// If already stored, nothing to do.
					} else {
						// We'll believe the file is already in HDFS because Teapot put it ... ( costly to confirm, so have faith )
						// Nothing to do.
					}
					// Set the column content to HDFS file, so it will reflect in CSV and target table file name
					columnContent = hdfsFileTargetFolder + "/" + columnContent;
				}
				
				/*
				if ( isFile(fileNameFromCSV) ) {
					lastImportedFile = fileNameFromCSV;
					if ( fileIds.get(fileNameFromCSV) == null ) {
						// Its a new file to store. Send to database and store it's ID to a list
						int fileId = importFile( csvDataFile.getExperimentSerial(), fileNameFromCSV, activity, instance );
						fileIds.put( fileNameFromCSV, fileId );
						fileNameFromCSV = String.valueOf( fileId );
					} else {
						// We've stored this file already! get it's ID from list
						fileNameFromCSV = String.valueOf( fileIds.get(fileNameFromCSV) );
					}
				} else {
					// This data value is not correspondent to any file we have.
					// But is the column a file domain for this table ?
					
					
						// Yes... set it as null
						logger.warn("the domain column " + columnName + " in table " + relationName + " has received no file");
						fileNameFromCSV = "null";
					}
				}
				
				*/
				// **********************************************************************************
				
				sb.append( prefix + columnContent );
				prefix = ",";
				
				
			}

			headerReady = true;
			contentLines.add( sb.toString() );
			sb.setLength(0);
			prefix = "";
			
		}
		parser.close();
		
		// At this point, we have all files stored in database and CSV data contains its 
		// key references instead its names.
		// Its time to store all csv data into target table...

		if ( contentLines.size() > 1 ) {
			log = "Inserting CSV data into table " + csvDataFile.getTargetTable() + "...";
			logger.debug("inserting "+contentLines.size()+" lines of CSV data into table " + table.getName() + "..." );
			
			new DataReceiver().receive( contentLines, macAddress, instance, activity, table, 
					csvDataFile, this, initialLoad );
			
			insertedLines = importedLines;
			logger.debug("done inserting CSV data into table " + table.getName() );
		} else {
			logger.debug("not enough data.");
		}
		
	}
	
	/**
	 * Decompress a file
	 */
	public void decompress( String compressedFile, String decompressedFile ) {
		compressedFile = compressedFile + ".gz";
		
		logger.debug("uncompressing " + compressedFile + "...");
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fileIn = new FileInputStream(compressedFile);
			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
			FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);
			int bytes_read;
			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bytes_read);
			}
			gZIPInputStream.close();
			fileOutputStream.close();
			logger.debug("file was decompressed successfully");
		} catch (IOException ex) {
			logger.error("error decompressing file: " + ex.getMessage() + ". Zero length file?" );
		}
	}

	
	/**
	 * Parse the XML file descriptor of session package
	 * This file describes all files uploaded by current session and
	 * the name of main CSV data file
	 * 
	 */
	private void parseXml( String descriptor ) throws Exception {
		String newDescriptor = descriptor + ".uncompressed";
		logger.debug("decompressing descriptor " + newDescriptor + "...");
		decompress( descriptor, newDescriptor );
		
		logger.debug("parsing descriptor " + newDescriptor + "...");
		FileXMLParser parser = new FileXMLParser();
		receivedFiles = parser.parseDescriptor( newDescriptor );
		ReceivedFile csvDataFile = null;
		
		if ( receivedFiles.size() > 0) {
			ReceivedFile any = receivedFiles.get(0);
			this.fragment = any.getFragment();
			this.instance = any.getInstance();
			this.activity = any.getActivity();
		}
		
		// Find the main CSV data file (sagi_output.txt for Teapot or any other if user manual load)
		logger.debug( receivedFiles.size() +  " files received on session " + sessionSerial );
		for( ReceivedFile receivedFile : receivedFiles ) {
			if ( receivedFile.getType().equals("FILE_TYPE_CSV") ) {
				// Take the CSV file
				logger.debug("will process csv from " + receivedFile.getFileName() );
				csvDataFile = receivedFile;
			}
		} 
		
		
		// If found, open it, store files to database and import CSV data
		if ( csvDataFile != null ) {
			importData( csvDataFile );
		} else {
			// We don't have a CSV sagi_output.txt, take any file record to log the operation and close the task.
			if ( receivedFiles.size() > 0) {
				ReceivedFile any = receivedFiles.get(0);
				mainCsvFile = any;
				logger.error("no csv data file in descriptor " + newDescriptor + ". finishing instance " + any.getInstance() );

				try {
					InstanceService ps = new InstanceService();
					Instance instance = ps.getInstance( any.getInstance() );
					
					ActivityService as = new ActivityService();
					Activity act = as.getActivity( any.getActivity() );
					
					RelationService relationService = new RelationService();
					Relation table = relationService.getTable( any.getTargetTable() );					
					
					ReceivedData rd = new ReceivedData(null, any.getMacAddress(), instance, act, table, any );
					NodesManager.getInstance().finishInstance( rd );				
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				logger.debug("done finishing instance " + any.getInstance() );
			} else {
				logger.error("SEVERE: no files found in descriptor " + newDescriptor + ". cannot process this response" );
			}
		}

	}
	
	
	
	@Override
	public void run() {
		logger.debug("starting new file importer for session " + tag );
		
		status = "WORKING";
		
		try {
			String descriptor = sessionContext + "/session.xml";
			File testDescriptor = new File( descriptor + ".gz" );
			if( !testDescriptor.exists() ) {
				throw new Exception("Descriptor " + descriptor + " not found");
			}

			parseXml( descriptor );
		
			logger.debug("session " + sessionSerial + " commited");
		} catch ( Exception e ) {
			logger.error("Error '" + e.getMessage() + "' while commiting session " + sessionSerial);
			cleanFiles();
		}
		
		try { 
			server.closeTransaction(sessionSerial); 
		} catch ( Exception e ) { 
			logger.error( e.getMessage() + " while removing cache folder for session " + sessionSerial);
		}
		
		log = "Finished.";
		status = "DONE";
		server.clean();
		logger.debug("importer " + tag + " finish.");
	}
	
	

	
	public Date getStartTime() {
		return startTime;
	}
}
