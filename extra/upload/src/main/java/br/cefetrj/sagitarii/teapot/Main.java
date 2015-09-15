package br.cefetrj.sagitarii.teapot;

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

import br.cefetrj.sagitarii.teapot.comm.Uploader;


public class Main {
	private static Logger logger = LogManager.getLogger( "br.cefetrj.sagitarii.teapot.Main" ); 
	private static Configurator configurator;
	

	public static void main( String[] args ) {
		try {
			configurator = new Configurator("config.xml");
			configurator.loadMainConfig();

			if ( configurator.useProxy() ) {
				logger.debug("Proxy: " + configurator.getProxyInfo().getHost() );
			}

			if ( args.length == 1 ) {
				if ( args[0].toLowerCase().equals("-gui") ) {
					new SwingFileUploadHTTP( configurator ).runUi();
				} else showGreetings(); 
			} else 
			
			if ( args.length == 4 ) {
				boolean start = ( args[0].toLowerCase().equals("-start") );
				if ( start ) {
					String experimentSerial = args[1];
					String userName = args[2];
					String password = args[3];
					
					System.out.println("Starting experiment " + experimentSerial + "...");
					SagitariiInterface si = new SagitariiInterface(configurator.getHostURL(), userName, password);
					String ret = si.startExperiment(experimentSerial);
					System.out.println( "Server response: " + ret );
					
				} else {
					String fileName = args[0];
					String relationName = args[1];
					String experimentSerial = args[2];
					String folderName = args[3];
					System.out.println("Uploading data. Wait...");
					new Uploader(configurator).uploadCSV(fileName, relationName, experimentSerial, folderName, configurator.getSystemProperties() );
					System.out.println("Done.");
					System.exit(0);
				} 
			}
		} catch (Exception e) {
			showGreetings();
		}

	}
	
	private static void showGreetings() {
		System.out.println("");
    	System.out.println("Sagitarii Upload Tool v1.1            15/09/2015");
    	System.out.println("Carlos Magno Abreu        magno.mabreu@gmail.com");
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("Use upload <file.csv> <target_table> <experiment_tag> <work_folder> ");
		System.out.println("          | -start <experiment_tag> <username> <password>");
		System.out.println("          | -gui");
		System.out.println("");
		System.out.println("     file.csv       : Your data file (just the file name).");			
		System.out.println("");
		System.out.println("     target_table   : The table where 'file.csv' content will be stored.");
		System.out.println("");
		System.out.println("     experiment_tag : The Experiment serial number.");
		System.out.println("                      You must create an Experiment using Sagitarii");			
		System.out.println("                      and then copy the serial number ( TagExec) ");			
		System.out.println("                      from Sagitarii web interface.");			
		System.out.println("");
		System.out.println("     work_folder    : Full path where 'file.csv' is in.");
		System.out.println("");
		System.out.println("     -start         : Will start Experiment 'experiment_tag' under");
		System.out.println("                      'username' credentials. This user must");			
		System.out.println("                      be a valid user in Sagitarii.");			
		System.out.println("");
		System.out.println("     -gui           : Will start the Graphical User Interface");
		System.out.println("                      When using the GUI you can leave 'Work Folder' blank.");			
		System.out.println("");
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("Note: You MUST put all files used by 'file.csv' in 'outbox' folder under");
		System.out.println("   this directory BEFORE call Upload Tool. Just create if you can't find it.");
		System.out.println("------------------------------------------------------------------------------");
	}

}
