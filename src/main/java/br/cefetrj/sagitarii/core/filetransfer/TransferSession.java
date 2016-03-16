package br.cefetrj.sagitarii.core.filetransfer;

import java.util.List;

public class TransferSession {
	private String sessionSerial;
	private List<FileImporter> importers;
	
	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public TransferSession( String sessionSerial ) {
		this.sessionSerial = sessionSerial;
	}
	
	public List<FileImporter> getImporters() {
		return importers;
	}
	
	public void setImporters(List<FileImporter> importers) {
		this.importers = importers;
	}
	
}
