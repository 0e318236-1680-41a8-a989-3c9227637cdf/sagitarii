package br.cefetrj.sagitarii.teapot.comm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import br.cefetrj.sagitarii.teapot.Logger;

public class MultiThreadUpload {
	private int maxThreadsRunning; 
	private long totalBytes = 0;
	
	public MultiThreadUpload( int maxThreadsRunning ) {
		this.maxThreadsRunning = maxThreadsRunning;
		
	}
	
	public long getTotalBytes() {
		return totalBytes;
	}
	
	public void upload( List<String> fileList, Logger logger, String storageAddress, 
			int storagePort, String targetTable, String experimentSerial, 
			String sessionSerial, String sourcePath ) {
		
		List< FutureTask<Long> > futureTasks = new ArrayList< FutureTask<Long> >();
		List<List<String>> partitions = splitList( fileList );
		ExecutorService executor = Executors.newFixedThreadPool( maxThreadsRunning );

		for( List<String> list : partitions ) {
			FTPUploadTask fut = new FTPUploadTask(list, logger, storageAddress, storagePort, 
					targetTable, experimentSerial, sessionSerial, sourcePath);
			
			FutureTask<Long> futureTask = new FutureTask<Long>( fut );
			executor.execute( futureTask );
			futureTasks.add( futureTask );
		}
		
		while ( true ) {
            try {
            	boolean done = true;
            	for ( FutureTask<Long> ft : futureTasks ) {
            		done = ( done && ( ft.isDone() || ft.isCancelled() ) ); 
            		if ( ft.isDone() ) {
            			//totalBytes = totalBytes + ft.get();
            		}
            	}
            	if ( done ) break;
            } catch ( Exception e ) {
            	
            }
		}
		executor.shutdown();
	}
	
	public List<List<String>> splitList( List<String> list ) {
		int partitionSize = list.size() / maxThreadsRunning;
		List<List<String>> partitions = new LinkedList<List<String>>();
		for (int i = 0; i < list.size(); i += partitionSize) {
			partitions.add(list.subList(i,
					Math.min(i + partitionSize, list.size())));
		}
		return partitions;
	}
	
}
