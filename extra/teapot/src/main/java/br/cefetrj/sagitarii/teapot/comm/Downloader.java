package br.cefetrj.sagitarii.teapot.comm;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import br.cefetrj.sagitarii.teapot.LogManager;
import br.cefetrj.sagitarii.teapot.Logger;
import br.cefetrj.sagitarii.teapot.ZipUtil;

public class Downloader {
	private Logger logger = LogManager.getLogger( this.getClass().getName() ); 

	/**
	 * Faz o download de um arquivo do servidor.
	 * 
	 */
	public void download( String from, String to, boolean decompress ) throws Exception {
		String fileName = to;
		if ( decompress ) {
			fileName = fileName + ".gz";
		}
		
		logger.debug("downloading " + fileName + "... may take some time.");
		logger.debug(" > " + from );
		
		URL link = new URL(from); 	
		InputStream in = new BufferedInputStream(link.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while (-1 != (n = in.read(buf) ) ) {
			out.write(buf, 0, n);
		}
		out.close();
		in.close();
		byte[] response = out.toByteArray();

		FileOutputStream fos = new FileOutputStream( fileName );
		fos.write(response);
		fos.close();
		
		File check = new File( fileName );
		if ( check.exists() ) {
			long size = check.length();
			logger.debug("done downloading " + fileName + ": " + size + " bytes");
			if ( size == 0 ) {
				throw new Exception(fileName + " is empty. Check original file.");
			}
			if ( decompress ) {
				ZipUtil.decompress(fileName, to);
				new File( fileName ).delete();
			}
		} else {
			throw new Exception("File "+fileName+" was not received! Check file in Sagitarii repository.");
		}
	}



}
