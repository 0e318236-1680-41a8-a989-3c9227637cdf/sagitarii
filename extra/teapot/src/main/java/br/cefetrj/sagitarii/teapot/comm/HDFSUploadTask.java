package br.cefetrj.sagitarii.teapot.comm;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.teapot.ZipUtil;

public class HDFSUploadTask implements Callable<Long> {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
    private List<String> fileNames;
    private String experimentSerial; 
	private String sessionSerial; 
	private String sourcePath;
	private FileSystem fs;
	private String configPath;
	
	public HDFSUploadTask(List<String> fileNames, String configPath, 
			String targetTable, String experimentSerial, 
			String sessionSerial, String sourcePath ) throws Exception {
		this.fileNames = fileNames;
		
		this.experimentSerial = experimentSerial;
		this.sessionSerial = sessionSerial;
		this.sourcePath = sourcePath;
		this.configPath = configPath;
		
		Configuration conf=new Configuration();

		conf.addResource(new Path( configPath + "core-site.xml") );
		conf.addResource(new Path( configPath + "hdfs-site.xml") );        

		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName() );
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName() );		
		fs = FileSystem.get(conf);
		
		logger.debug("create");
	}
	
	private long uploadFiles() throws Exception {
		logger.debug("sending " + fileNames.size() + " files to HDFS in session " +
					sessionSerial + " experiment " + experimentSerial + ": " + sourcePath );
		long size = 0;
		int indexFile = 1;
		for ( String fileName : fileNames) {
			logger.debug("[" + indexFile + "] will send " + fileName );
			indexFile++;
		
			String newFileName = fileName + ".gz";
			
			logger.debug("compressing " + fileName + "...");
			ZipUtil.compress(fileName, newFileName);
			logger.debug("done compressing " + fileName + ".");
		
			File localFile = new File(newFileName);
			
			size = size + localFile.length();
			
	        logger.debug("sending [" + sessionSerial + "] " + localFile.getName() + " with size of " + localFile.length() + " bytes..." );
	        logger.debug("Strategy: HDFS from " + configPath );
			
			//String hFileName = localFile.getName();
			String remoteFile = "/" + experimentSerial + "/" + sessionSerial + "/";			
			
	        logger.debug("HDFS put " + newFileName + " to " + remoteFile);
	        copyFileToHdfs(newFileName,remoteFile);
		}
        
        return size;
	}

	@Override
	public Long call() throws Exception {
		logger.debug("start");
		try {
			uploadFiles();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return 0L;
	}
	
	public void copyFileToHdfs( String source, String target ) throws Exception {
		Path root = new Path("/");
		fs.setWorkingDirectory( root );
		
		Path pathSrc = new Path(source);
		Path hdfsTargetFolder = new Path(target);

		logger.debug("create HDFS folder " + target );
		fs.mkdirs(hdfsTargetFolder);
		
		fs.copyFromLocalFile(pathSrc, hdfsTargetFolder);  
		logger.debug("all done. HDFS copy: " + source + " to " + hdfsTargetFolder );

	}
	
}
