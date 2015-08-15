
package br.cefetrj.sagitarii.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.misc.DatabaseInfo;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Action (value = "doMirrorDatabase", results = { @Result (type="redirect", location = "index", name = "ok") } ) 

@ParentPackage("default")
public class DoMirrorDatabaseAction extends BasicActionClass {
	private String targetHost; 
	private String targetDb; 
	private String targetUser; 
	private String targetPassword; 
	private String targetPort; 
	
	private String sourceHost; 
	private String sourceDb; 
	private String sourceUser; 
	private String sourcePassword; 
	private String sourcePort; 
	
	
	public String execute () {
		
		System.out.println(sourceHost+ " " +  sourceDb+ " " +  sourceUser+ " " +  sourcePassword+ " " +  sourcePort);
		System.out.println(targetHost+ " " +  targetDb+ " " +  targetUser+ " " +  targetPassword+ " " +  targetPort);
		
		try {
			
			DatabaseInfo source = new DatabaseInfo(sourceHost, sourceDb, sourceUser, sourcePassword, sourcePort);
			DatabaseInfo target = new DatabaseInfo(targetHost, targetDb, targetUser, targetPassword, targetPort);
			
			RelationService rs = new RelationService();
			rs.copyDatabase(source, target);
			setMessageText( "All data was copied from "+sourceDb+"@"+sourceHost+":" + sourcePort + " to "+targetDb+"@"+targetHost+":" + targetPort );
		} catch ( Exception e ) {
			setMessageText( e.getMessage() );
		} 
		
		return "ok";
	}


	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}


	public void setTargetDb(String targetDb) {
		this.targetDb = targetDb;
	}


	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}


	public void setTargetPassword(String targetPassword) {
		this.targetPassword = targetPassword;
	}


	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}


	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}


	public void setSourceDb(String sourceDb) {
		this.sourceDb = sourceDb;
	}


	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}


	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}


	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}


	
}
