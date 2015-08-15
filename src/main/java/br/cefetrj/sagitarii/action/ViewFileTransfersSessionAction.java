
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.filetransfer.FileImporter;
import br.cefetrj.sagitarii.core.filetransfer.FileReceiverManager;
import br.cefetrj.sagitarii.core.filetransfer.FileSaver;

@Action (value = "viewFileTransfersSession", 
	results = { 
		@Result ( location = "viewFileTransfersSession.jsp", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewFileTransfersSessionAction extends BasicActionClass {
	private List<FileSaver> savers;
	private List<FileImporter> importers;
	private String sessionSerial;
	
	public String execute () {
		
		try {
			savers = FileReceiverManager.getInstance().getSaversInTransfer( sessionSerial );
			importers = FileReceiverManager.getInstance().getImportersBySession( sessionSerial );
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return "ok";
	}

	public List<FileSaver> getSavers() {
		return savers;
	}
	
	public void setSessionSerial(String sessionSerial) {
		this.sessionSerial = sessionSerial;
	}
	
	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public List<FileImporter> getImporters() {
		return importers;
	}
	
}
