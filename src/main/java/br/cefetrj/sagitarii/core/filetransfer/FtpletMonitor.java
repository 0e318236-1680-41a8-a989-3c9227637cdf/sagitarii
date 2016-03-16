package br.cefetrj.sagitarii.core.filetransfer;

import java.io.IOException;

import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FtpletMonitor implements Ftplet {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );

	@Override
	public FtpletResult afterCommand(FtpSession arg0, FtpRequest arg1,	FtpReply arg2) throws FtpException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FtpletResult beforeCommand(FtpSession arg0, FtpRequest arg1) 	throws FtpException, IOException {
		try {
			logger.debug("client "+ arg0.getSessionId().toString() + " " + arg0.getUser().getName()+" command " + arg1.getCommand() + " " + arg1.getArgument() );
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FtpletContext arg0) throws FtpException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FtpletResult onConnect(FtpSession arg0) throws FtpException,	IOException {
		try {
			logger.debug("client "+ arg0.getSessionId().toString() + " " + arg0.getUser().getName()+" connect ");
		} catch ( Exception e ) { }
		return null;
	}

	@Override
	public FtpletResult onDisconnect(FtpSession arg0) throws FtpException,	IOException {
		try {
			logger.debug("client "+ arg0.getSessionId().toString() + " " + arg0.getUser().getName()+" disconnect ");
		} catch ( Exception e ) { }
		return null;
	}

}
