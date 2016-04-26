package br.cefetrj.sagitarii.core.filetransfer;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HDFSUploadTask implements Callable<Long> {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private String sessionSerial; 
	private String fullFilePath;
	private String hdfsFileTargetFolder;

	private FileSystem fs;
	
	public HDFSUploadTask(String fullFilePath, String hdfsFileTargetFolder, String configPath, 
			String sessionSerial) throws Exception {
		
		Configuration conf=new Configuration();
		this.sessionSerial = sessionSerial;
		this.hdfsFileTargetFolder = hdfsFileTargetFolder;
		this.fullFilePath = fullFilePath;

		conf.addResource(new Path( configPath + "core-site.xml") );
		conf.addResource(new Path( configPath + "hdfs-site.xml") );        

		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName() );
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName() );		
		fs = FileSystem.get(conf);
		
		logger.debug("create");
	}
	
	private long uploadFiles() throws Exception {
		logger.debug("sending " + fullFilePath + " file to HDFS in session " +
					sessionSerial + ": " +  hdfsFileTargetFolder );

		File localFile = new File(fullFilePath);
		long size = localFile.length();
        
		copyFileToHdfs(fullFilePath,hdfsFileTargetFolder);
        
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
		//Path pathDest = new Path(target);
		
		//String targetFolder = new File(target).getAbsolutePath();
		
		Path hdfsTargetFolder = new Path(target);
		fs.mkdirs(hdfsTargetFolder);
		logger.debug("create HDFS folder " + target );
		fs.copyFromLocalFile(pathSrc, hdfsTargetFolder);  
		logger.debug("all done. HDFS copy: " + source + " to " + hdfsTargetFolder );

		/*
		FileStatus fileStatus = fs.getFileStatus(pathDest);
		BlockLocation[] blkLocations = fs.getFileBlockLocations(pathDest, 0, fileStatus.getLen());
		int blkCount = blkLocations.length;
		for (int i=0; i < blkCount; i++) {
			String[] hosts = blkLocations[i].getHosts();
			for( String ss : hosts ) {
				System.out.println(" >>> " + ss );
			}
		}
		*/			
		
	}
	
}
