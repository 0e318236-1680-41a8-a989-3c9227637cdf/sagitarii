package br.cefetrj.sagitarii.core.filetransfer;


public class TransferSession {
	private String sessionSerial;
	private FileImporter importer;
	
	public String getSessionSerial() {
		return sessionSerial;
	}
	
	public TransferSession( String sessionSerial ) {
		this.sessionSerial = sessionSerial;
	}
	
	public FileImporter getImporter() {
		return importer;
	}
	
	public void setImporter(FileImporter importer) {
		this.importer = importer;
	}
	
}
