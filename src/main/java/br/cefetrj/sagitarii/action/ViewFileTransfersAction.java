
package br.cefetrj.sagitarii.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.filetransfer.FileReceiverManager;
import br.cefetrj.sagitarii.core.filetransfer.TransferSession;

@Action (value = "viewFileTransfers", 
	results = { 
		@Result ( location = "viewFileTransfers.jsp", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewFileTransfersAction extends BasicActionClass {
	private List<TransferSession> sessions;
	
	public String execute () {
		
		sessions = new ArrayList<TransferSession>();
		try {
			sessions = FileReceiverManager.getInstance().getTransferSessions();
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		
		return "ok";
	}

	public List<TransferSession> getSessions() {
		return sessions;
	}

}
