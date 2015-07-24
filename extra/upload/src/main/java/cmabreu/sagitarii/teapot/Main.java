package cmabreu.sagitarii.teapot;

/**
 * Copyright 2015 Carlos Magno Abreu
 * magno.mabreu@gmail.com 
 *
 * Licensed under the Apache  License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required  by  applicable law or agreed to in  writing,  software
 * distributed   under the  License is  distributed  on  an  "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the  specific language  governing  permissions  and
 * limitations under the License.
 * 
 */

 /*
	http://www.codejava.net/coding/swing-application-to-upload-files-to-http-server-with-progress-bar
 */
 
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.teapot.comm.Uploader;


public class Main {
	private static Logger logger = LogManager.getLogger( "cmabreu.sagitarii.teapot.Main" ); 
	private static Configurator configurator;
	

	public static void main( String[] args ) {
		try {
			System.out.println("");
	    	System.out.println("Sagitarii Upload Tool v1.0            24/06/2015");
	    	System.out.println("Carlos Magno Abreu        magno.mabreu@gmail.com");
			System.out.println("------------------------------------------------");
			System.out.println("");
			System.out.println("Use upload <file.csv> <target_table> <experiment_tag> <work_folder>");
			System.out.println("");

			configurator = new Configurator("config.xml");
			configurator.loadMainConfig();

			if ( configurator.useProxy() ) {
				logger.debug("Proxy: " + configurator.getProxyInfo().getHost() );
			}

			if ( args.length == 4 ) {
				String fileName = args[0];
				String relationName = args[1];
				String experimentSerial = args[2];
				String folderName = args[3];
				
				new Uploader(configurator).uploadCSV(fileName, relationName, experimentSerial, folderName, configurator.getSystemProperties() );
				System.exit(0);
			} else {
				new SwingFileUploadHTTP( configurator ).runUi();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
