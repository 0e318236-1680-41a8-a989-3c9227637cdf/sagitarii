package br.cefetrj.sagitarii.persistence;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import br.cefetrj.sagitarii.core.config.Configurator;

public class HDFS {
	private FileSystem fs;
	
	public class HdfsData {
		private String hdfsCapacity = "";
		private String hdfsUsed = "";
		private String hdfsRemaining = "";
		
		public String getHdfsCapacity() {
			return hdfsCapacity;
		}
		
		public String getHdfsRemaining() {
			return hdfsRemaining;
		}
		
		public String getHdfsUsed() {
			return hdfsUsed;
		}
		
	}
	
	public List<String> getNodesStoringFile( String fileFullPath ) throws Exception {
		List<String> result = new ArrayList<String>();
		
		Path path = new Path( fileFullPath );
		
		FileStatus fileStatus = fs.getFileStatus( path );
		
		BlockLocation[] blkLocations = fs.getFileBlockLocations(path, 0, fileStatus.getLen());
		int blkCount = blkLocations.length;
		for (int i=0; i < blkCount; i++) {
			String[] hosts = blkLocations[i].getHosts();
			for( String ss : hosts ) {
				result.add( ss );
			}
		}
		return result;
	}
	
	public boolean deleteDirectory( String path ) throws Exception  {
		return fs.delete( new Path ( path ), true );
	}
	
	public HDFS() throws Exception {
		Configuration conf=new Configuration();

		String hadoopConfigPath = Configurator.getInstance().getHadoopConfigPath();
		
		conf.addResource(new Path( hadoopConfigPath + "core-site.xml") );
		conf.addResource(new Path( hadoopConfigPath + "hdfs-site.xml") );        

		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName() );
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName() );		
		fs = FileSystem.get(conf);
	}
	
	public HdfsData getHDFSSpace() throws Exception {
		HdfsData hd = new HdfsData();
		hd.hdfsCapacity = formatSize ( fs.getStatus().getCapacity() ); 
		hd.hdfsUsed = formatSize ( fs.getStatus().getUsed() ) ; 
		hd.hdfsRemaining = formatSize ( fs.getStatus().getRemaining() ) ;	
		return hd;
	}
	
	
	public InputStream getFile( String file ) throws Exception {
		String firstSlash = "";
		if ( !file.startsWith("/") ) {
			firstSlash = "/";
		}
		Path inFile = new Path( firstSlash + file + ".gz");
		
		System.out.println("****************************************");
		System.out.println(" > " + inFile );
		System.out.println("****************************************");
		
		InputStream fsd = fs.open(inFile);
		return fsd;
	}
	
	private String formatSize( long size ) {
	    String hrSize = null;

	    double b = size;
	    double k = size/1024.0;
	    double m = ((size/1024.0)/1024.0);
	    double g = (((size/1024.0)/1024.0)/1024.0);
	    double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

	    DecimalFormat dec = new DecimalFormat("0.00");

	    if ( t>1 ) {
	        hrSize = dec.format(t).concat(" TB");
	    } else if ( g>1 ) {
	        hrSize = dec.format(g).concat(" GB");
	    } else if ( m>1 ) {
	        hrSize = dec.format(m).concat(" MB");
	    } else if ( k>1 ) {
	        hrSize = dec.format(k).concat(" KB");
	    } else {
	        hrSize = dec.format(b).concat(" Bytes");
	    }

	    return hrSize;
	}	
	
	
}
