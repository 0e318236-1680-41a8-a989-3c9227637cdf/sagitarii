package br.cefetrj.sagitarii.persistence;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import br.cefetrj.sagitarii.core.config.Configurator;

public class HDFS {
	private FileSystem fs;
	
	public class HdfsData {
		private long hdfsCapacity = 0;
		private long hdfsUsed = 0;
		private long hdfsRemaining = 0;
		
		public long getHdfsCapacity() {
			return hdfsCapacity;
		}
		
		public long getHdfsRemaining() {
			return hdfsRemaining;
		}
		
		public long getHdfsUsed() {
			return hdfsUsed;
		}
		
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
		hd.hdfsCapacity = fs.getStatus().getCapacity() / 1048576 ; 
		hd.hdfsUsed = fs.getStatus().getUsed() / 1048576; 
		hd.hdfsRemaining = fs.getStatus().getRemaining() / 1048576;	
		return hd;
	}
	
	
}
