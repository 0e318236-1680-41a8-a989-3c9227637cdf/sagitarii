package br.cefetrj.sagitarii.persistence.exceptions;

public class DatabaseConnectException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatabaseConnectException(String message){
		super(message);
	}
	
}
