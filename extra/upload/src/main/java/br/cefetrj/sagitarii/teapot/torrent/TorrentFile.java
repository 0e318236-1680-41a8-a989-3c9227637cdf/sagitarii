package br.cefetrj.sagitarii.teapot.torrent;

public class TorrentFile {
	private String fileName = "";
	private String fileContent = "";
	private String hash = ""; 
	public TorrentFile( String fileName, String fileContent, String hash ) {
		this.fileContent = fileContent;
		this.fileName = fileName;
		this.hash = hash;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileContent() {
		return fileContent;
	}
	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	

}
