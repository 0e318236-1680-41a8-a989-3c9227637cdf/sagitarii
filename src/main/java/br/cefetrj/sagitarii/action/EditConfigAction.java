package br.cefetrj.sagitarii.action;

import java.io.PrintWriter;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.misc.PathFinder;

@Action (value = "editConfig", 
results = { 
	@Result ( location = "editConfig.jsp", name = "ok")
} ) 

@ParentPackage("default")
public class EditConfigAction  {
	private Configurator config;
	private String op;
	private String userName;
	private String password;
	private String databaseName;
	private int poolIntervalSeconds;
	private int fileReceiverPort;
	private int fileReceiverChunkBufferSize;
	
	public String execute(){

		try {
			config = Configurator.getInstance("config.xml");
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		if ( op != null ) {
			try {
				String configFile = PathFinder.getInstance().getPath() + "/WEB-INF/classes/config.xml";

				config.setUserName( userName );
				config.setDatabaseName( databaseName );
				config.setPassword( password );
				config.setPoolIntervalSeconds(poolIntervalSeconds);
				config.setFileReceiverChunkBufferSize(fileReceiverChunkBufferSize);
				config.setFileReceiverPort(fileReceiverPort);
				
				PrintWriter out = new PrintWriter( configFile );
				out.println( config.toXml() );
				out.close();
				
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		return "ok";
	}

	public Configurator getConfig() {
		return config;
	}
	
	public void setOp(String op) {
		this.op = op;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPoolIntervalSeconds(int poolIntervalSeconds) {
		this.poolIntervalSeconds = poolIntervalSeconds;
	}

	public void setFileReceiverPort(int fileReceiverPort) {
		this.fileReceiverPort = fileReceiverPort;
	}

	public void setFileReceiverChunkBufferSize(int fileReceiverChunkBufferSize) {
		this.fileReceiverChunkBufferSize = fileReceiverChunkBufferSize;
	}
	
	
}
